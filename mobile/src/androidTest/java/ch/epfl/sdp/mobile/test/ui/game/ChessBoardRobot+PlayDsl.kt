package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.test.application.chess.engine.GameScope

/**
 * Performs all the steps from the [block], updating the robot internally. If a step fails, it will
 * simply be ignored, but the ulterior steps may still be applied if they're valid.
 *
 * This method should be used cautiously, since it slightly breaks encapsulation (the [GameScope] is
 * designed to work on the model rather than the view). However, it can be extremely useful to play
 * some well-known games on a real board.
 *
 * @receiver the [ChessBoardRobot] to which steps will be applied.
 * @param rotated true if the black player is at the bottom of the screen.
 * @param block the block of steps to perform.
 */
fun ChessBoardRobot.play(rotated: Boolean = false, block: GameScope.() -> Unit) =
    ChessBoardRobotGameScope(this, rotated).run(block)

/**
 * An implementation of a [GameScope] which delegates moves to a [ChessBoardRobot].
 *
 * @param robot the underlying [ChessBoardRobot].
 * @param rotated true if the black player is at the bottom of the screen.
 */
private class ChessBoardRobotGameScope(
    private val robot: ChessBoardRobot,
    private val rotated: Boolean,
) : GameScope {

  override fun tryMove(from: Position, delta: Delta) {
    robot.performInput(rotated = rotated) {
      val to = from + delta ?: return@performInput // Ignore out of bounds moves.

      click(from.x, from.y)
      click(to.x, to.y)
    }
  }

  override fun tryPromote(from: Position, delta: Delta, rank: Rank) {
    tryMove(from, delta)

    // Attempt to click on the right icon and confirm.
    val description =
        when (rank) {
          Rank.King -> robot.strings.boardPieceKing
          Rank.Queen -> robot.strings.boardPieceQueen
          Rank.Rook -> robot.strings.boardPieceRook
          Rank.Bishop -> robot.strings.boardPieceBishop
          Rank.Knight -> robot.strings.boardPieceKnight
          Rank.Pawn -> robot.strings.boardPiecePawn
        }
    robot.onNodeWithContentDescription(description).performClick()
    robot.onNodeWithLocalizedText { gamePromoteConfirm }.performClick()
  }
}
