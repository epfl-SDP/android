package ch.epfl.sdp.mobile.application.speech

import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.*
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.*
import ch.epfl.sdp.mobile.infrastructure.sound.SoundPlayer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeech
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A facade which provides access to functions to perform some voice recognition.
 *
 * @property speechFactory the [SpeechRecognizerFactory] which is used internally by this
 * [SpeechFacade].
 * @property textToSpeechFactory the [TextToSpeechFactory] which is used internally by this
 * [SpeechFacade].
 * @param soundPlayer the [SoundPlayer] used to play chess sounds by this [SpeechFacade].
 * @param dataStoreFactory the [DataStoreFactory] which is used to persist user
 * parameters/preferences of the speech facade.
 */
class SpeechFacade(
    private val speechFactory: SpeechRecognizerFactory,
    private val textToSpeechFactory: TextToSpeechFactory,
    private val soundPlayer: SoundPlayer,
    dataStoreFactory: DataStoreFactory,
) {

  /** The result of a call to [SpeechFacade.recognize]. */
  sealed interface RecognitionResult {

    /** Indicates that a failure occurred. */
    sealed interface Failure : RecognitionResult {

      /** Indicates that a failure occurred and no speech was recognized. */
      object Internal : Failure
    }

    /**
     * Indicates a success of recognition. The available [results] are sorted by decreasing score
     * (the most relevant results come first).
     *
     * @param results the [List] of possible results.
     */
    data class Success(val results: List<String>) : RecognitionResult
  }

  /**
   * Starts voice recognition, and returns the associated [RecognitionResult].
   *
   * @return the [RecognitionResult] from the recognition request.
   */
  suspend fun recognize(): RecognitionResult = suspendCancellableCoroutine { cont ->
    val recognizer = speechFactory.createSpeechRecognizer()

    /** Cleans up the recognizer. */
    fun cleanup() {
      recognizer.stopListening()
      recognizer.destroy()
    }

    recognizer.setListener(
        object : SpeechRecognizer.Listener {
          override fun onError() {
            cleanup()
            cont.resume(Failure.Internal)
          }

          override fun onResults(results: List<String>) {
            cleanup()
            cont.resume(Success(results))
          }
        },
    )
    recognizer.startListening()
    cont.invokeOnCancellation { cleanup() }
  }

  /** Companion object that stores the data store key for text to speech parameters. */
  object DataStoreKeys {

    /** Key used in data store for text to speech enabling setting. */
    const val TextToSpeechEnabled = "textToSpeech_enabled"
  }

  /** The [DataStore] instance in which the preferences are stored. */
  private val dataStore: DataStore<Preferences>

  /** Parameter key holding the enabled value of the text to speech. */
  private val keyTextToSpeechEnabled: Key<Boolean>

  init {
    val (prefs, factory) = dataStoreFactory.createPreferencesDataStore()
    dataStore = prefs
    keyTextToSpeechEnabled = factory.boolean(DataStoreKeys.TextToSpeechEnabled)
  }

  /**
   * Class representing settings of the text to speech.
   * @param enabled true if the text to speech is enabled, false otherwise.
   * @param facade [SpeechFacade] currently provided speech facade.
   */
  class TextToSpeechSettings(val enabled: Boolean, private val facade: SpeechFacade) {

    /**
     * Updates the datastore.
     * @param scope [UpdateScope] scope under which the update is done.
     */
    suspend fun update(scope: UpdateScope.() -> Unit) =
        facade.dataStore.edit { scope(UpdateScope(facade, it)) }

    /**
     * Class that represents the scope under which the preferences are updated/set.
     * @param facade [SpeechFacade] current facade.
     * @param preferences [MutablePreferences] preferences to be updated/set.
     */
    class UpdateScope(
        private val facade: SpeechFacade,
        private val preferences: MutablePreferences,
    ) {
      /**
       * Associates the value of enables to its key in the datastore.
       * @param value values to be associated.
       */
      fun enabled(value: Boolean) = preferences.set(facade.keyTextToSpeechEnabled, value)
    }
  }

  /** Returns a single [Flow] of [TextToSpeechSettings]. */
  fun textToSpeechSettings(): Flow<TextToSpeechSettings> =
      dataStore.data.map { prefs ->
        val enabled = prefs[keyTextToSpeechEnabled] ?: true
        TextToSpeechSettings(enabled, this)
      }

  private var textToSpeech: TextToSpeech? = null
  private val mutex = Mutex()

  /**
   * Synthesizes the given text.
   * @param text to synthesize.
   */
  suspend fun synthesize(text: String) {
    val tts =
        mutex.withLock {
          val tts = textToSpeech ?: textToSpeechFactory.create()
          textToSpeech = tts
          tts
        }

    if (textToSpeechSettings().first().enabled) {
      tts.speak(text)
      soundPlayer.playChessSound()
    }
  }
}
