package ch.epfl.sdp.mobile.infrastructure.tts.android

import android.content.Context
import android.os.Bundle
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
    private val volume: VolumeLevel = VolumeLevel.On
) : TextToSpeechFactory {
  override suspend fun create(): TextToSpeech = suspendCoroutine { cont ->
    var speech by notNull<NativeTextToSpeech>()
    val callback =
        NativeTextToSpeech.OnInitListener { status ->
          when (status) {
            NativeTextToSpeech.SUCCESS -> cont.resume(AndroidTextToSpeech(speech, volume))
            NativeTextToSpeech.ERROR -> cont.resumeWithException(IllegalStateException())
          }
        }

    speech = NativeTextToSpeech(context, callback)
  }
}

class AndroidTextToSpeech(
    private val tts: NativeTextToSpeech,
    private val volume: VolumeLevel = VolumeLevel.On,
) : TextToSpeech {
  init {
    // Set up properties like voice, languages ...
    tts.language = Locale.ENGLISH
    tts.voice = null
    tts.setSpeechRate(0f)
    tts.setPitch(0f)
  }

  override fun speak(text: String) {
    // Set volume for this tts request
    val params = Bundle()
    params.putFloat(NativeTextToSpeech.Engine.KEY_PARAM_VOLUME, volume.level)

    tts.speak(text, NativeTextToSpeech.QUEUE_ADD, params, null)
  }


  override fun isSpeaking(): Boolean {
    return tts.isSpeaking
  }

  override fun stop() {
    tts.stop()
  }

  override fun shutdown() {
    tts.shutdown()
  }
}

enum class VolumeLevel(val level: Float) {
  On(1f),
  Muted(0f)
}
