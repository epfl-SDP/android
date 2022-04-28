package ch.epfl.sdp.mobile.ui.speech_recognition

typealias Rule = (List<String>) -> Boolean

/** Object that contains all the chess speech filter rules for chess pieces */
object ChessSpeechFilterRules {
  /**
   * Data class that holds rule for a specific chess piece
   * @property chessPiece as string
   * @property rule rule of the chessPiece
   */
  data class ChessSpeechRule(val chessPiece: String, val rule: Rule)

  val rulesSet =
      setOf(
          ChessSpeechRule("pawn", this::filterForPawn),
          ChessSpeechRule("king", this::filterForKing),
          ChessSpeechRule("rook", this::filterForRook))

  /**
   * Filter rule for Pawn
   * @param speech list of speech' words
   * @return true if the rule applies to the speech, false otherwise
   */
  private fun filterForPawn(speech: List<String>): Boolean {

    return speech.any { sp -> sp.startsWithAny("bon", "pon") }
  }
  /**
   * Filter rule for King
   * @param speech list of speech' words
   * @return true if the rule applies to the speech, false otherwise
   */
  private fun filterForKing(speech: List<String>): Boolean {
    return speech.any { sp -> sp.endsWithAny("inc", "ink", "ing") }
  }
  /**
   * Filter rule for Rook
   * @param speech list of speech' words
   * @return true if the rule applies to the speech, false otherwise
   */
  private fun filterForRook(speech: List<String>): Boolean {
    return speech.any { sp -> sp.endsWithAny("ouk", "uk", "uch", "och") }
  }

  /**
   * @param suffixes variable argument of suffixes
   * @return true if and only if [this] ends with any of the suffixes
   */
  private fun String.endsWithAny(vararg suffixes: String): Boolean {
    return suffixes.any { this.endsWith(it) }
  }

  /**
   * @param prefixes variable argument of suffixes
   * @return true if and only if [this] starts with any of the prefixes
   */
  private fun String.startsWithAny(vararg prefixes: String): Boolean {
    return prefixes.any { this.startsWith(it) }
  }
}
