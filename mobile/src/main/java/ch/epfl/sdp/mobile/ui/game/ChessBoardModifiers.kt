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

fun Modifier.checkerboard(
    color: Color = Color.Unspecified,
    cells: Int = 8,
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

fun Modifier.grid(
    color: Color = Color.Unspecified,
    width: Dp = 2.dp,
    cells: Int = 8,
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
