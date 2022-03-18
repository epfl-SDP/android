package ch.epfl.sdp.mobile.application.chess

/**
 * An interface representing the current [Game], which contains a [Board] of pieces and on which
 * some [NextStep] should be performed to move forward for the chess game.
 */
interface Game {

  /** The current [Board], which contains some pieces and should be rendered to the user. */
  val board: Board<Piece<Color>>

  /** Represents the [NextStep] that must be performed on this [Game]. */
  val nextStep: NextStep
}
