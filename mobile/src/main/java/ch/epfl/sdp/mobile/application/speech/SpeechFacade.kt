package ch.epfl.sdp.mobile.application.speech

import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.*
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.*
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
 * @param speechFactory the [SpeechRecognizerFactory] which is used internally by this
 * [SpeechFacade].
 */
class SpeechFacade(
    private val speechFactory: SpeechRecognizerFactory,
    private val textToSpeechFactory: TextToSpeechFactory,
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

  object DataStoreKeys {
    const val TextToSpeechEnabled = "textToSpeech_enabled"
  }

  /** The [DataStore] instance in which the preferences are stored. */
  private val dataStore: DataStore<Preferences>

  private val keyTextToSpeechEnabled: Key<Boolean>

  init {
    val (prefs, factory) = dataStoreFactory.createPreferencesDataStore()
    dataStore = prefs
    keyTextToSpeechEnabled = factory.boolean(DataStoreKeys.TextToSpeechEnabled)
  }

  class TextToSpeechSettings(val enabled: Boolean, private val facade: SpeechFacade) {

    suspend fun update(scope: UpdateScope.() -> Unit) =
        facade.dataStore.edit { scope(UpdateScope(facade, it)) }

    class UpdateScope(
        private val facade: SpeechFacade,
        private val preferences: MutablePreferences,
    ) {
      fun enabled(value: Boolean) = preferences.set(facade.keyTextToSpeechEnabled, value)
    }
  }

  fun textToSpeechSettings(): Flow<TextToSpeechSettings> =
      dataStore.data.map { prefs ->
        val enabled = prefs[keyTextToSpeechEnabled] ?: true
        TextToSpeechSettings(enabled, this)
      }

  private var textToSpeech: TextToSpeech? = null
  private val mutex = Mutex()

  suspend fun synthesize(text: String) {
    val tts =
        mutex.withLock {
          val tts = textToSpeech ?: textToSpeechFactory.create()
          textToSpeech = tts
          tts
        }

    if (textToSpeechSettings().first().enabled) {
      tts.speak(text)
    }
  }
}
