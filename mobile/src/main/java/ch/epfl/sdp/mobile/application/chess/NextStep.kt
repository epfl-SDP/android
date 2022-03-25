package ch.epfl.sdp.mobile.application.chess

/**
 * An interface representing the next steps that could be performed on an existing [Game] instance.
 * Because [Game] is immutable, the only way to move forward in a chess game is to perform a
 * [NextStep] which actually updates the state of the [Game] and returns an updated [Game].
 */
sealed interface NextStep {

  /**
   * A [NextStep] which indicates that the current player may not perform any moves without putting
   * themselves in a check position, but is not currently in check.
   */
  object Stalemate : NextStep

  /**
   * A [NextStep] which indicates that the game is over.
   *
   * @param winner the [Color] of the player who won.
   */
  data class Checkmate(val winner: Color) : NextStep

  /**
   * A [NextStep] which indicates that the [Game] expects the player with color [turn] to perform a
   * move.
   *
   * @param turn the [Color] of the player who should perform a move next.
   * @param inCheck returns true if the current player is in check.
   * @param move provides the next [Game] depending on which position was moved by how much.
   */
  data class MovePiece(
      val turn: Color,
      val inCheck: Boolean,
      val move: (Position, Delta) -> Game,
  ) : NextStep
}
