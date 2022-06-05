package ch.epfl.sdp.mobile.ui.game.classic

import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.unit.*
import ch.epfl.sdp.mobile.ui.drawParagraph
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import kotlin.math.roundToInt

/**
 * A [Modifier] which draws a square checkerboard centered in this layout node. The top-left cell
 * will be transparent.
 *
 * @param color the color with which the board will be drawn.
 * @param cells the number of cells of the grid.
 */
fun Modifier.checkerboard(
    color: Color = Color.Unspecified,
    cells: Int = ChessBoardCells,
): Modifier = composed {
  val squareColor = color.takeOrElse { LocalContentColor.current }
  drawBehind {
    val origin = size.center - Offset(size.minDimension / 2, size.minDimension / 2)
    val squareSize = size.minDimension / cells

    for (i in 0 until cells) {
      for (j in 0 until cells) {
        val squareOffset = origin + Offset(i * squareSize, j * squareSize)
        if ((i + j) % 2 == 1) {
          drawRect(
              color = squareColor,
              topLeft = squareOffset,
              size = Size(squareSize, squareSize),
          )
        }
      }
    }
  }
}

/**
 * A [Modifier] which draws a grid.
 *
 * @param color the color of the grid.
 * @param width the width of the grid lines.
 * @param cells the number of cells of the grid.
 */
fun Modifier.grid(
    color: Color = Color.Unspecified,
    width: Dp = 2.dp,
    cells: Int = ChessBoardCells,
): Modifier = composed {
  val lineColor = color.takeOrElse { LocalContentColor.current }
  drawBehind {
    val origin = size.center - Offset(size.minDimension / 2, size.minDimension / 2)
    val squareSize = size.minDimension / cells

    for (i in 0..cells) {
      drawLine(
          color = lineColor,
          start = origin + Offset(i * squareSize, 0f),
          end = origin + Offset(i * squareSize, size.minDimension),
          strokeWidth = width.toPx(),
          cap = StrokeCap.Round,
      )
      drawLine(
          color = lineColor,
          start = origin + Offset(0f, i * squareSize),
          end = origin + Offset(size.minDimension, i * squareSize),
          strokeWidth = width.toPx(),
          cap = StrokeCap.Round,
      )
    }
  }
}

/**
 * A [Modifier] which draws a [Set] of [Position].
 *
 * @param positions the positions to draw on the component.
 * @param color the [Color] of the circles.
 * @param diameter the size of each circle.
 * @param cells the number of cells in the grid.
 */
fun Modifier.actions(
    positions: Set<Position>,
    color: Color = Color.Unspecified,
    diameter: Dp = 16.dp,
    cells: Int = ChessBoardCells
): Modifier = composed {
  val surfaceColor = color.takeOrElse { LocalContentColor.current }
  cells(positions = positions, cells = cells) {
    onDrawInFront { drawCircle(color = surfaceColor, radius = diameter.toPx() / 2) }
  }
}

/**
 * A [Modifier] which draws a position that is currently in check.
 *
 * @param position the position that should be drawn.
 * @param color the [Color] of the background for check.
 * @param cells the number of cells in the grid.
 */
fun Modifier.check(
    position: Position?,
    color: Color = Color.Unspecified,
    cells: Int = ChessBoardCells,
): Modifier = composed {
  val fillColor = color.takeOrElse { LocalContentColor.current }
  cells(
      positions = position?.let(::setOf) ?: emptySet(),
      cells = cells,
  ) { onDrawBehind { drawRect(fillColor) } }
}

/**
 * A [Modifier] which fills the cells for the provided [position]s.
 *
 * @param position the position that should be drawn.
 * @param color the [Color] of the cell
 * @param cells the number of cells in the grid.
 */
fun Modifier.lastMove(
    position: Set<Position>,
    color: Color = Color.Unspecified,
    cells: Int = ChessBoardCells,
): Modifier = composed {
  val fillColor = color.takeOrElse { LocalContentColor.current }.copy(alpha = ContentAlpha.disabled)
  cells(
      positions = position,
      cells = cells,
  ) { onDrawBehind { drawRect(color = fillColor) } }
}

/** The duration of a cycle of the selection dashed border animation. */
private const val SelectionDurationMillis = DefaultDurationMillis * 4

/**
 * A [Modifier] which draws an animated dashed border for the provide [Position].
 *
 * @param position the position that should be drawn.
 * @param color the [Color] of the animated border.
 * @param width the width of the border stroke.
 * @param cells the number of cells in the grid.
 */
fun Modifier.selection(
    position: Position?,
    color: Color = Color.Unspecified,
    width: Dp = 4.dp,
    cells: Int = ChessBoardCells,
): Modifier = composed {
  val lineColor = color.takeOrElse { LocalContentColor.current }
  val transition = rememberInfiniteTransition()
  val progress by
      transition.animateFloat(
          initialValue = 0f,
          targetValue = 1f,
          animationSpec =
              infiniteRepeatable(
                  tween(
                      durationMillis = SelectionDurationMillis,
                      easing = LinearEasing,
                  ),
              ),
      )
  cells(
      positions = position?.let(::setOf) ?: emptySet(),
      cells = cells,
  ) {
    onDrawBehind {
      val phase = size.width / 3
      val style =
          Stroke(
              width = width.toPx(),
              pathEffect =
                  dashPathEffect(
                      phase = -2 * progress * phase,
                      intervals = floatArrayOf(phase, phase),
                  ),
          )
      drawRect(color = lineColor, style = style)
    }
  }
}

/** A [text] which will be displayed within a cell, using a certain [alignment]. */
private data class Letter(val text: String, val alignment: Alignment)

/** A [Map] of [Position] to [String] that should be displayed in this specific cell. */
private val PositionsToLetters = buildMap {
  for ((index, row) in (0..7).reversed().withIndex()) {
    this[Position(-1, index)] = Letter((row + 1).toString(), BiasAlignment(0.5f, 0f))
  }
  for ((index, char) in ('a'..'h').withIndex()) {
    this[Position(index, 8)] = Letter(char.toString(), BiasAlignment(0f, -0.5f))
  }
}

/**
 * A [Modifier] which draws the text indications for the rows and the columns of the board.
 *
 * @param color the [Color] of the border text.
 * @param angle the angle to apply to the text.
 */
fun Modifier.letters(
    color: Color = Color.Unspecified,
    angle: () -> Float = { 0f },
): Modifier = composed {
  val textColor = color.takeOrElse { LocalContentColor.current }
  val style = LocalTextStyle.current
  val loader = LocalFontLoader.current
  val direction = LocalLayoutDirection.current
  cells(positions = PositionsToLetters.keys) {
    val paragraphs =
        PositionsToLetters.mapValues { (_, letter) ->
          val (text, alignment) = letter
          val intrinsic =
              ParagraphIntrinsics(
                  text = text,
                  style = style,
                  density = this,
                  resourceLoader = loader,
              )
          Paragraph(
              paragraphIntrinsics = intrinsic,
              maxLines = 1,
              ellipsis = false,
              width = intrinsic.maxIntrinsicWidth,
          ) to alignment
        }
    onDrawInFront {
      val (paragraph, alignment) = requireNotNull(paragraphs[it])
      val paragraphOffset = IntSize(paragraph.width.roundToInt(), paragraph.height.roundToInt())
      val sizeOffset = IntSize(size.width.roundToInt(), size.height.roundToInt())
      val topLeft = alignment.align(paragraphOffset, sizeOffset, direction).toOffset()
      withTransform(
          transformBlock = {
            rotate(
                degrees = angle(),
                pivot = topLeft + (Offset(paragraph.width, paragraph.height) / 2f),
            )
          },
      ) {
        drawParagraph(
            paragraph = paragraph,
            color = textColor,
            topLeft = topLeft,
        )
      }
    }
  }
}

/**
 * A [Modifier] which draws each cell passed as a [Position].
 *
 * @param positions the [Set] of position which should be drawn.
 * @param cells the number of cells which should be displayed per side.
 * @param onDraw the block in which drawing is performed.
 */
private fun Modifier.cells(
    positions: Set<Position>,
    cells: Int = ChessBoardCells,
    onDraw: CellsContentDrawScope.() -> CellsDrawResult,
) = drawWithContent { onDraw(CellsContentDrawScope(this, positions, cells)) }

/**
 * Handles to a drawing environments which enables caching based on the resolved size.
 *
 * @param scope the underlying [CacheDrawScope].
 * @param positions the positions which will be drawn.
 * @param cells the number of cells which should be displayed per side.
 *
 * @see CacheDrawScope
 */
class CellsContentDrawScope
internal constructor(
    private val scope: ContentDrawScope,
    private val positions: Set<Position>,
    private val cells: Int,
) : Density by scope {

  /**
   * Draws all the positions using the [block] into the given [DrawScope].
   *
   * @receiver the [DrawScope] into which the positions are drawn.
   * @param block the block which draws individual positions.
   */
  private /* inline */ fun DrawScope.drawPositions(
      block: DrawScope.(Position) -> Unit,
  ) {
    val squareSize = size.minDimension / cells
    for (position in positions) {
      val (x, y) = position
      withTransform(
          transformBlock = {
            val left = x * squareSize
            val top = y * squareSize
            inset(
                left = left,
                top = top,
                right = size.width - (left + squareSize),
                bottom = size.height - (top + squareSize),
            )
          },
          drawBlock = { block(position) },
      )
    }
  }

  /**
   * Issues drawing commands to be executed before the layout content is drawn.
   *
   * @param block the block of drawing commands.
   */
  fun onDrawBehind(block: DrawScope.(Position) -> Unit): CellsDrawResult {
    with(scope) {
      drawPositions(block)
      drawContent()
    }
    return CellsDrawResult
  }

  /**
   * Issues drawing commands to be executed after the layout content is drawn.
   *
   * @param block the block of drawing commands.
   */
  fun onDrawInFront(block: DrawScope.(Position) -> Unit): CellsDrawResult {
    with(scope) {
      drawContent()
      drawPositions(block)
    }
    return CellsDrawResult
  }
}

/**
 * An object which guarantees that a drawing method of [CellsContentDrawScope] will be properly
 * called.
 *
 * @see DrawResult
 */
object CellsDrawResult
