package ch.epfl.sdp.mobile.infrastructure.tts

interface TextToSpeechFactory {
    suspend fun create()
}