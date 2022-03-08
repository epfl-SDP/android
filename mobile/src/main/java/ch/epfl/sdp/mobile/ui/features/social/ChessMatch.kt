package ch.epfl.sdp.mobile.ui.features.social

class ChessMatch(
    val adv: String,
    val matchResult: MatchResult,
    val cause: MatchResult.Reason,
    val numberOfMoves: Int
)

enum class MatchResult {
  WIN,
  LOSS,
  TIE;
  enum class Reason {
    CHECKMATE,
    FORFEIT
  }
}
