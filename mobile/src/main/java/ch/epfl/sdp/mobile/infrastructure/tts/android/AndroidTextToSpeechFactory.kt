package ch.epfl.sdp.mobile.infrastructure.tts.android

import android.content.Context
import android.speech.tts.TextToSpeech as NativeTextToSpeech
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeech
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory
import java.lang.IllegalStateException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.Delegates.notNull

/**
 * A factory that produces android built-in implementation of the text to speech.
 * @param context [Context] context of the application.
 */
class AndroidTextToSpeechFactory(
    private val context: Context,
) : TextToSpeechFactory {
  override suspend fun create(): TextToSpeech = suspendCoroutine { cont ->
    var speech by notNull<NativeTextToSpeech>()
    val callback =
        NativeTextToSpeech.OnInitListener { status ->
          when (status) {
            NativeTextToSpeech.SUCCESS -> cont.resume(AndroidTextToSpeech(speech))
            NativeTextToSpeech.ERROR -> cont.resumeWithException(IllegalStateException())
          }
        }

    speech = NativeTextToSpeech(context, callback)
  }
}

/**
 * An adaptation from the android text to speech to Pawnies [TextToSpeech].
 * @param androidTextToSpeech [NativeTextToSpeech] instance of the native android text to speech.
 */
class AndroidTextToSpeech(
    private val androidTextToSpeech: NativeTextToSpeech,
) : TextToSpeech {

  init {
    // Settings for the android text to speech
    androidTextToSpeech.language = Locale.ENGLISH
  }

  override fun speak(text: String) {
    // Set volume for this tts request
    androidTextToSpeech.speak(text, NativeTextToSpeech.QUEUE_ADD, null, null)
  }

  override fun stop() {
    androidTextToSpeech.stop()
  }

  override fun shutdown() {
    androidTextToSpeech.shutdown()
  }
}
