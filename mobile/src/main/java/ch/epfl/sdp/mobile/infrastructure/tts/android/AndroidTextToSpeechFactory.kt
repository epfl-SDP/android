package ch.epfl.sdp.mobile.infrastructure.tts.android
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeech
import android.speech.tts.TextToSpeech as NativeTextToSpeech

class AndroidTextToSpeechFactory(private val tts: NativeTextToSpeech): TextToSpeech {
    override fun speak(text: String) {
        tts.speak(text, NativeTextToSpeech.QUEUE_ADD, null, null )
    }


}