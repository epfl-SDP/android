package ch.epfl.sdp.mobile.ui.features.social

class ChessMatch(val adversary: String, val matchResult: MatchResult, val numberOfMoves: Int)

sealed interface MatchResult {
  enum class Reason {
    CHECKMATE,
    FORFEIT
  }
}

data class Win(val reason: MatchResult.Reason) : MatchResult

data class Loss(val reason: MatchResult.Reason) : MatchResult

object Tie : MatchResult
