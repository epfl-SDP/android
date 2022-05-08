package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.PawniesColors

/**
 * A divider with a dash pattern, which may be used to split content vertically.
 *
 * @param modifier the [Modifier] for this composable.
 * @param color the [Color] of the dashed line.
 * @param width the width of the dashed line.
 * @param interval the size of the intervals.
 */
@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    width: Dp = 2.dp,
    interval: Dp = 4.dp,
) {
  val currentColor = color.takeOrElse { PawniesColors.Green100 }
  Canvas(modifier.fillMaxWidth().height(width)) {
    drawLine(
        color = currentColor,
        start = Offset(0f, width.toPx() / 2),
        end = Offset(size.width, width.toPx()),
        strokeWidth = width.toPx(),
        cap = StrokeCap.Round,
        pathEffect = dashPathEffect(floatArrayOf(interval.toPx(), interval.toPx())),
    )
  }
}
