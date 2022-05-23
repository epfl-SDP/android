package ch.epfl.sdp.mobile.infrastructure.tts

import android.speech.tts.UtteranceProgressListener

interface TextToSpeech {

  interface UtteranceListener {
    fun onStart(utteranceId: String?)
    fun onDone(utteranceId: String?)
    fun onError(utteranceId: String?)
  }

  fun setOnUtteranceListener(listener: UtteranceListener)

  fun speak(text: String, utteranceId: String?)

  fun isSpeaking(): Boolean

  fun stop()

  fun shutdown()
}
