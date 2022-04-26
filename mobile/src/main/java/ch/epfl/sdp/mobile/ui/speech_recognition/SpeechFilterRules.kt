package ch.epfl.sdp.mobile.ui.speech_recognition

object SpeechFilterRules {

    val rules = setOf(this::filterForKing, this::filterForPawn)

    private fun filterForPawn(speech: String): Boolean {
        return speech.startsWith("bon") || speech.startsWith("pon")
    }

    private fun filterForKing(speech: String): Boolean {
        return speech.endsWith("ing") || speech.endsWith("ink")
    }
}
