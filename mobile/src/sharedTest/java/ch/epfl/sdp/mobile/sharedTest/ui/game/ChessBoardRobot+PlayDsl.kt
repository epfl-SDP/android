package ch.epfl.sdp.mobile.sharedTest.ui.game

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.sharedTest.application.chess.engine.GameScope
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/**
 * Performs all the steps from the [block], updating the robot internally. If a step fails, it will
 * simply be ignored, but the ulterior steps may still be applied if they're valid.
 *
 * This method should be used cautiously, since it slightly breaks encapsulation (the [GameScope] is
 * designed to work on the model rather than the view). However, it can be extremely useful to play
 * some well-known games on a real board.
 *
 * @receiver the [ChessBoardRobot] to which steps will be applied.
 * @param block the block of steps to perform.
 */
fun ChessBoardRobot.play(block: GameScope.() -> Unit) {
  ChessBoardRobotGameScope(this).apply(block)
}

/**
 * An implementation of a [GameScope] which delegates moves to a [ChessBoardRobot].
 *
 * @param robot the underlying [ChessBoardRobot].
 */
private class ChessBoardRobotGameScope(private val robot: ChessBoardRobot) : GameScope {

  override fun tryMove(from: Position, delta: Delta) {
    robot.performInput {
      val to = from + delta ?: return@performInput // Ignore out of bounds moves.

      val actualFrom = ChessBoardState.Position(from.x, from.y)
      val actualTo = ChessBoardState.Position(to.x, to.y)

      click(actualFrom.x, actualFrom.y)
      click(actualTo.x, actualTo.y)
    }
  }
}
