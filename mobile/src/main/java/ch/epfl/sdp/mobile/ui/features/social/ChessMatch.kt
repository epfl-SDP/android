package ch.epfl.sdp.mobile.ui.features.social

class ChessMatch(val adversary: String, val matchResult: MatchResult, val numberOfMoves: Int)

fun main(): Unit {

  println(MatchResult.Reason.CHECKMATE.reason)
}

sealed class MatchResult() {
  enum class Reason(val reason: String) {
    CHECKMATE("checkmate"),
    FORFEIT("forfeit")
  }
}

class Win(val reason: Reason) : MatchResult() {
  override fun toString(): String {
    return "Won"
  }
}

class Loss(val reason: Reason) : MatchResult() {
  override fun toString(): String {
    return "Lost"
  }
}

class Tie : MatchResult()
