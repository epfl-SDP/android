package ch.epfl.sdp.mobile.sharedTest.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import ch.epfl.sdp.mobile.sharedTest.ui.AbstractRobot
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.contentDescription
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * An interface representing the operations which are available when interacting with the chess
 * board. Inputs are performed in a coordinate system starting at the top left of the chessboard,
 * with the x axis going towards the right and the y axis going towards the bottom.
 *
 * If the x or y coordinates are outside of the [0..8) range, inputs will NOT be clamped. This means
 * you can drop pieces outside of the board.
 */
interface ChessBoardRobotInputScope {

  /**
   * Advances the time by the given amount in milliseconds. The next events will be delayed by this
   * amount.
   *
   * @param durationMillis the amount of time waited.
   */
  fun advanceEventTime(durationMillis: Long)

  /**
   * Puts the pointer with the given id down on this cell.
   *
   * @param x the x coordinate at which the pointer goes down.
   * @param y the y coordinate at which the pointer goes down.
   * @param pointerId the identifier of the pointer.
   */
  fun down(x: Int, y: Int, pointerId: Int = 0)

  /**
   * Moves the pointer with the given id to this cell.
   *
   * @param x the x coordinate of the target cell.
   * @param y the y coordinate of the target cell.
   * @param pointerId the identifier of the pointer.
   */
  fun moveTo(x: Int, y: Int, pointerId: Int = 0)

  /**
   * Moves the pointer with the given id by a relative amount of cells.
   *
   * @param x the x delta.
   * @param y the y delta.
   * @param pointerId the identifier of the pointer.
   */
  fun moveBy(x: Int, y: Int, pointerId: Int = 0)

  /**
   * Moves the pointer with the given id up, finishing a gesture.
   *
   * @param pointerId the identifier of the pointer.
   */
  fun up(pointerId: Int = 0)
}

/**
 * Performs a click gesture with the pointer with the given id at a specific position.
 *
 * @param x the x coordinate of the target cell.
 * @param y the y coordinate of the target cell.
 * @param pointerId the identifier of the pointer.
 */
fun ChessBoardRobotInputScope.click(x: Int, y: Int, pointerId: Int = 0) {
  down(x = x, y = y, pointerId = pointerId)
  // Required to send a move event. See TouchInjectionScope.click() for details.
  moveBy(x = 0, y = 0, pointerId = pointerId)
  up(pointerId = pointerId)
}

/**
 * Performs a drag gesture between two positions with the pointer with the given id.
 *
 * @param from the original [ChessBoardState.Position].
 * @param to the target [ChessBoardState.Position].
 * @param pointerId the identifier of the pointer.
 */
fun ChessBoardRobotInputScope.drag(
    from: ChessBoardState.Position,
    to: ChessBoardState.Position,
    pointerId: Int = 0,
) {
  down(x = from.x, y = from.y, pointerId = pointerId)
  moveTo(x = to.x, y = to.y, pointerId = pointerId)
  up(pointerId = pointerId)
}

/**
 * A robot which may be used to perform actions on a [ch.epfl.sdp.mobile.ui.game.ClassicChessBoard]
 * composable.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class ChessBoardRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /** Returns the [SemanticsNodeInteraction] corresponding to the chessboard. */
  private fun onChessBoard(): SemanticsNodeInteraction {
    return onNodeWithLocalizedContentDescription { boardContentDescription }
  }

  /**
   * Performs the inputs specified in the [ChessBoardRobotInputScope] on the chessboard.
   *
   * @param scope the [ChessBoardRobotInputScope] in which the inputs are performed.
   */
  fun performInput(scope: ChessBoardRobotInputScope.() -> Unit) {
    val sizeInfo = BoardSizeInfo(onChessBoard())
    onChessBoard().performTouchInput { SizeInfoChessBoardInputScope(sizeInfo, this).apply(scope) }
  }

  /**
   * Asserts that a piece with the given [color] and [rank] is present at the given position. This
   * will essentially check that the center of the piece is present in a specific cell.
   *
   * @param x the first coordinate of the cell.
   * @param y the second coordinate of the cell.
   * @param color the [ChessBoardState.Color] to check for.
   * @param rank the [ChessBoardState.Rank] to check for.
   */
  fun assertHasPiece(x: Int, y: Int, color: ChessBoardState.Color, rank: ChessBoardState.Rank) {
    val boardBounds = onChessBoard().fetchSemanticsNode().boundsInRoot
    val sizeInfo = BoardSizeInfo(onChessBoard())
    val matcher =
        SemanticsMatcher("positionInBounds") { piece ->
          val bounds =
              Rect(
                      offset = boardBounds.topLeft,
                      size = Size(sizeInfo.squareSize, sizeInfo.squareSize),
                  )
                  .translate(x * sizeInfo.squareSize, y * sizeInfo.squareSize)
          bounds.contains(piece.boundsInRoot.center)
        }
    onAllNodesWithLocalizedContentDescription {
          boardPieceContentDescription(
              color.contentDescription(this),
              rank.contentDescription(this),
          )
        }
        .assertAny(matcher)
  }
}

/**
 * A helper class which computes some size information related to a board.
 *
 * @param interaction the [SemanticsNodeInteraction] to access the board.
 */
private class BoardSizeInfo(interaction: SemanticsNodeInteraction) {
  /** The size of the board. */
  val size = interaction.fetchSemanticsNode().size

  /** The int size corresponding to half a square. */
  val halfSquare = Offset(size.height / 16f, size.width / 16f)

  /** The dimensions of a square of the board. */
  val squareSize = minOf(size.height, size.width) / 8f
}

/**
 * An implementation of [ChessBoardRobotInputScope] which delegates interactions to a
 * [TouchInjectionScope].
 *
 * @param sizeInfo the [BoardSizeInfo] to access basic information from the board.
 * @param scope the [TouchInjectionScope] in which the interactions are performed.
 */
private class SizeInfoChessBoardInputScope(
    private val sizeInfo: BoardSizeInfo,
    private val scope: TouchInjectionScope,
) : ChessBoardRobotInputScope {

  override fun advanceEventTime(durationMillis: Long) =
      scope.advanceEventTime(durationMillis = durationMillis)

  override fun down(x: Int, y: Int, pointerId: Int) =
      scope.down(
          pointerId = pointerId,
          position = Offset(x * sizeInfo.squareSize, y * sizeInfo.squareSize) + sizeInfo.halfSquare,
      )

  override fun moveTo(x: Int, y: Int, pointerId: Int) =
      scope.moveTo(
          pointerId = pointerId,
          position = Offset(x * sizeInfo.squareSize, y * sizeInfo.squareSize) + sizeInfo.halfSquare,
      )

  override fun moveBy(x: Int, y: Int, pointerId: Int) =
      scope.moveBy(
          pointerId = pointerId,
          delta = Offset(x * sizeInfo.squareSize, y * sizeInfo.squareSize),
      )

  override fun up(pointerId: Int) = scope.up(pointerId = pointerId)
}
