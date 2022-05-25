package ch.epfl.sdp.mobile.infrastructure.tts


interface TextToSpeech {

  fun speak(text: String)

  fun stop()

  fun shutdown()
}
