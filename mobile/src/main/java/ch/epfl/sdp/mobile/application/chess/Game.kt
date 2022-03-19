package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.moves.Action

/**
 * An interface representing the current [Game], which contains a [Board] of pieces and on which
 * some [NextStep] should be performed to move forward for the chess game.
 */
interface Game {

  /** The current [Board], which contains some pieces and should be rendered to the user. */
  val board: Board<Piece<Color>>

  /** Represents the [NextStep] that must be performed on this [Game]. */
  val nextStep: NextStep

  /**
   * Returns a [Sequence] of the possible [Action] for the provided [Position]. If the [Position]
   * does not have a [Piece] or it's not this player's turn, the resulting sequence might be empty.
   *
   * @param position the position for which the available [Action]s are queried.
   * @return the [Sequence] of available actions.
   */
  fun actions(position: Position): Sequence<Action>
}
