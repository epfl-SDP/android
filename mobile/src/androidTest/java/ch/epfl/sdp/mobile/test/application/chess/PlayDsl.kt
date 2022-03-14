package ch.epfl.sdp.mobile.test.application.chess

import ch.epfl.sdp.mobile.application.chess.Delta
import ch.epfl.sdp.mobile.application.chess.Game
import ch.epfl.sdp.mobile.application.chess.NextStep
import ch.epfl.sdp.mobile.application.chess.Position

/**
 * An interface which provides some easy-to-use access to some operations which may be performed on
 * a [Game].
 */
interface GameScope {

  /** Tries to move a piece from the current [Position] with the provided [Delta]. */
  fun tryMove(from: Position, delta: Delta)

  /** An alias to [tryMove]. */
  operator fun Position.plusAssign(delta: Delta) = tryMove(this, delta)
}

private class MutableGameScope(var game: Game) : GameScope {

  /** Reads the [NextStep] as a [NextStep.MovePiece]. */
  private val nextStepAsMovePieceOrNull: NextStep.MovePiece?
    get() = game.nextStep as? NextStep.MovePiece

  /** Updates the game with the provided [Game] instance if it's not null. */
  private fun Game?.tryUpdate() {
    this?.let { game = it }
  }

  override fun tryMove(from: Position, delta: Delta) {
    nextStepAsMovePieceOrNull?.move?.invoke(from, delta).tryUpdate()
  }
}

/**
 * Performs all the steps from the [block], and returns the updated [Game]. If a step fails, it will
 * simply be ignored, but the ulterior steps may still be applied if they're valid.
 *
 * @receiver the initial [Game], to which steps will be applied.
 * @param block the block of steps to perform.
 * @return the updated [Game] instance.
 */
fun Game.play(block: GameScope.() -> Unit): Game {
  return MutableGameScope(this).also(block).game
}
