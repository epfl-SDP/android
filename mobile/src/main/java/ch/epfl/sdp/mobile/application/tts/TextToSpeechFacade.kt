package ch.epfl.sdp.mobile.application.tts

import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory

class TextToSpeechFacade(private val factory: TextToSpeechFactory) {
    suspend fun speak(text: String) {
        val synthesizer = factory.create()
        synthesizer.speak(text, utteranceId = /*TODO*/)
    }

}