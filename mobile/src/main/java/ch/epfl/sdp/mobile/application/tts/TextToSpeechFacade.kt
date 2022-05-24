package ch.epfl.sdp.mobile.application.tts

import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory

class TextToSpeechFacade(private val factory: TextToSpeechFactory) {
    val synthesizer = suspend { factory.create() }
}