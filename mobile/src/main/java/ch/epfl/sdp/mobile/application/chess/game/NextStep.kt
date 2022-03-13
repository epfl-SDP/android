package ch.epfl.sdp.mobile.application.chess.game

import ch.epfl.sdp.mobile.application.chess.Color
import ch.epfl.sdp.mobile.application.chess.Delta
import ch.epfl.sdp.mobile.application.chess.Position

/**
 * An interface representing the next steps that could be performed on an existing [Game] instance.
 * Because [Game] is immutable, the only way to move forward in a chess game is to perform a
 * [NextStep] which actually updates the state of the [Game] and returns an updated [Game].
 */
sealed interface NextStep {

  /**
   * A [NextStep] which indicates that the [Game] expects the player with color [turn] to perform a
   * move.
   *
   * @param turn the [Color] of the player who should perform a move next.
   * @param move provides the next [Game] depending on which position was moved by how much.
   */
  data class MovePiece(val turn: Color, val move: (Position, Delta) -> Game) : NextStep
}
