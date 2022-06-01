package ch.epfl.sdp.mobile.ui.social

/** Some information about a chess match. */
interface ChessMatch {

  /** The adversary in the match. */
  val adversary: String

  /** The current match result. */
  val matchResult: MatchResult

  /** The number of played moves. */
  val numberOfMoves: Int
}

/** The possible match results. */
sealed interface MatchResult {

  /** An enumeration which represents the possible reasons for match results. */
  enum class Reason {
    CHECKMATE,
    FORFEIT
  }
}

/**
 * A [MatchResult] which represents a win.
 *
 * @property reason the [MatchResult.Reason] for the win.
 */
data class Win(val reason: MatchResult.Reason) : MatchResult

/**
 * A [MatchResult] which represents a loss.
 *
 * @property reason the [MatchResult.Reason] for the loss.
 */
data class Loss(val reason: MatchResult.Reason) : MatchResult

/** A [MatchResult] which represents a tie. */
object Tie : MatchResult

/** A [MatchResult] which represents that it's the current player's turn to play. */
object YourTurn : MatchResult

/** A [MatchResult] which represents that it's the other player's turn to play. */
object OtherTurn : MatchResult
