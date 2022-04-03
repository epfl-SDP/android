package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
 * A [Modifier] which draws a [Set] of [ChessBoardState.Position].
 *
 * @param positions the positions to draw on the component.
 * @param color the [Color] of the circles.
 * @param diameter the size of each circle.
 * @param cells the number of cells in the grid.
 */
fun Modifier.actions(
    positions: Set<ChessBoardState.Position>,
    color: Color = Color.Unspecified,
    diameter: Dp = 16.dp,
    cells: Int = ChessBoardCells
): Modifier = composed {
  val surfaceColor = color.takeOrElse { LocalContentColor.current }
  cells(positions = positions, cells = cells) {
    drawCircle(color = surfaceColor, radius = diameter.toPx() / 2)
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
    position: ChessBoardState.Position?,
    color: Color = Color.Unspecified,
    cells: Int = ChessBoardCells,
): Modifier = composed {
  val fillColor = color.takeOrElse { LocalContentColor.current }
  cells(
      positions = position?.let(::setOf) ?: emptySet(),
      cells = cells,
  ) { drawRect(fillColor) }
}

/** The duration of a cycle of the selection dashed border animation. */
private const val SelectionDurationMillis = DefaultDurationMillis * 4

/**
 * A [Modifier] which draws an animated dashed border for the provide [ChessBoardState.Position].
 *
 * @param position the position that should be drawn.
 * @param color the [Color] of the animated border.
 * @param width the width of the border stroke.
 * @param cells the number of cells in the grid.
 */
fun Modifier.selection(
    position: ChessBoardState.Position?,
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

/**
 * A [Modifier] which calls [onDraw] for each cell passed as a [ChessBoardState.Position].
 *
 * @param positions the [Set] of position which should be drawn.
 * @param cells the number of cells which should be displayed per side.
 * @param onDraw the [DrawScope] in which drawing operations should be performed for each cell.
 */
private fun Modifier.cells(
    positions: Set<ChessBoardState.Position>,
    cells: Int = ChessBoardCells,
    onDraw: DrawScope.() -> Unit,
): Modifier = drawBehind {
  val origin = size.center - Offset(size.minDimension / 2, size.minDimension / 2)
  val squareSize = size.minDimension / cells
  for ((x, y) in positions) {
    withTransform(
        transformBlock = {
          val left = origin.x + x * squareSize
          val top = origin.y + y * squareSize
          inset(
              left = left,
              top = top,
              right = size.width - (left + squareSize),
              bottom = size.height - (top + squareSize),
          )
        },
        drawBlock = onDraw,
    )
  }
}
