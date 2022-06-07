package ch.epfl.sdp.mobile.application.speech

typealias Rule = (String) -> String?

/** Object that contains all the chess speech filter rules for chess pieces. */
object ChessSpeechFilterRules {

  /**
   * Data class that holds rule for a specific chess piece.
   * @property chessPiece as string.
   * @property rule rule of the chessPiece.
   */
  data class ChessSpeechRule(val chessPiece: String, val rule: Rule)

  /** The set of conversion rules for pawns, kings and rooks. */
  val rulesSet =
      setOf(
          ChessSpeechRule("pawn", this::tryConvertPawn),
          ChessSpeechRule("king", this::tryConvertKing),
          ChessSpeechRule("rook", this::tryConvertRook),
      )

  /**
   * Convert rule for Pawn.
   * @param token token that we want to change.
   * @return "pawn" if it can be transformed otherwise null.
   */
  private fun tryConvertPawn(token: String): String? {
    return if (token.endsOrStartsWithAny(String::startsWith, "bon", "pon")) "pawn" else null
  }

  /**
   * Convert rule for King.
   * @param token token that we want to change.
   * @return "king" if it can be transformed otherwise null.
   */
  private fun tryConvertKing(token: String): String? {
    return if (token.endsOrStartsWithAny(String::endsWith, "inc", "ink", "ing")) "king" else null
  }

  /**
   * Convert rule for Rook.
   * @param token token that we want to change.
   * @return "rook" if it can be transformed otherwise null.
   */
  private fun tryConvertRook(token: String): String? {
    return if (token.endsOrStartsWithAny(String::endsWith, "ouk", "uk", "uch", "och")) "rook"
    else null
  }

  /**
   * Auxiliary method used for applying checks to a speech word ending or starting with a variable.
   * number of suffixes/prefixes arguments.
   * @param endsOrStartWith ending or starting predicate method.
   * @param suffixesOrPrefixes variable argument of suffixes xor prefixes.
   * @return true if and only if [this] ends or starts with any of the suffixes/prefixes.
   */
  private fun String.endsOrStartsWithAny(
      endsOrStartWith: String.(String) -> Boolean,
      vararg suffixesOrPrefixes: String,
  ): Boolean {
    return suffixesOrPrefixes.any { this.endsOrStartWith(it) }
  }
}
