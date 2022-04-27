package ch.epfl.sdp.mobile.ui.speech_recognition

typealias Rule = (List<String>) -> Boolean

object ChessSpeechFilterRules {

  data class ChessSpeechRule(val chessPiece: String, val rule: Rule)

  val rulesSet =
      setOf(
          ChessSpeechRule("pawn", this::filterForPawn),
          ChessSpeechRule("king", this::filterForKing))

  private fun filterForPawn(speech: List<String>): Boolean {
    return speech.any { it.startsWith("bon") || it.startsWith("pon") }
  }

  private fun filterForKing(speech: List<String>): Boolean {
    return speech.any { it.endsWith("inc") || it.endsWith("ink") }
  }
}
