package ch.epfl.sdp.mobile.ui.features.social

class ChessMatch(val adversary: String, val matchResult: MatchResult, val numberOfMoves: Int)

sealed class MatchResult(val reason: Reason) {
  constructor() : this(Reason.NO_REASON)

  enum class Reason() {
    NO_REASON,
    CHECKMATE,
    FORFEIT
  }
}

class Win(reason: Reason) : MatchResult(reason)

class Loss(reason: Reason) : MatchResult(reason)

object Tie : MatchResult()
