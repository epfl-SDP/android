package ch.epfl.sdp.mobile.ui.game

import androidx.compose.material.LocalContentColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
  drawBehind {
    val origin = size.center - Offset(size.minDimension / 2, size.minDimension / 2)
    val squareSize = size.minDimension / cells
    val halfSquare = Offset(squareSize, squareSize) / 2f
    for ((x, y) in positions) {
      drawCircle(
          color = surfaceColor,
          radius = diameter.toPx() / 2,
          center = origin + Offset(x * squareSize, y * squareSize) + halfSquare,
      )
    }
  }
}
