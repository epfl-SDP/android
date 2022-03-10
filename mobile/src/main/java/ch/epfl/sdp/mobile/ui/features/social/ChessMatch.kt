package ch.epfl.sdp.mobile.ui.features.social

class ChessMatch(val adversary: String, val matchResult: MatchResult, val numberOfMoves: Int)

sealed class MatchResult() {
  enum class Reason(val reason: String) {
    CHECKMATE("checkmate"),
    FORFEIT("forfeit")
  }
}

class Win(val reason: Reason) : MatchResult() {
  override fun toString(): String = "Won"
}

class Loss(val reason: Reason) : MatchResult() {
  override fun toString(): String = "Lost"
}

class Tie : MatchResult()
