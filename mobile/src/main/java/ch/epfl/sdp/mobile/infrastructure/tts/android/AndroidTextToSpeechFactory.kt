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

class AndroidTextToSpeech(
    private val tts: NativeTextToSpeech,
) : TextToSpeech {
  init {
    // Set up properties like voice, languages ...
    tts.language = Locale.ENGLISH
    //    tts.voice = null
    //    tts.setSpeechRate(0f)
    //    tts.setPitch(0f)
  }

  override fun speak(text: String) {
    // Set volume for this tts request
    tts.speak(text, NativeTextToSpeech.QUEUE_ADD, null, null)
  }

  override fun stop() {
    tts.stop()
  }

  override fun shutdown() {
    tts.shutdown()
  }
}
