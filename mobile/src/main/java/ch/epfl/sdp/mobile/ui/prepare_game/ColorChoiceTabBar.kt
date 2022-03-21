package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*

@Preview
@Composable
fun preview() {

  PawniesTheme { ColorChoiceBar() }
}

enum class TabColors(val backgroundColor: Color, val textColor: Color) {
  SelectedColors(PawniesColors.Green800, PawniesColors.Orange200),
  UnselectedColors(PawniesColors.Green100, PawniesColors.Green800)
}

// interface ColorChoiceTabBarState {
//  var selectedColorChoice: ColorChoice
// }
//
// private class ColorChoiceTabBarStateImpl() : ColorChoiceTabBarState {
//  override var selectedColorChoice by mutableStateOf(ColorChoice.WHITE)
// }
//
// @Composable
// fun rememberColorChoiceTabBarState(): ColorChoiceTabBarState {
//  return remember { ColorChoiceTabBarStateImpl() }
// }

@Composable
fun ColorChoiceBar(modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  var color by remember { mutableStateOf(ColorChoice.WHITE) }

  Row(modifier = modifier) {
    ColorChoiceTabItem(
        text = strings.pregameWhiteColor,
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        onClick = { println("White selected ")
                    color = ColorChoice.WHITE },
        selected = color == ColorChoice.WHITE)
    ColorChoiceTabItem(
        text = strings.pregameBlackColor,
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        onClick = {
            println("Black selected ")
            color = ColorChoice.BLACK },
        selected = color == ColorChoice.BLACK)
  }
}

@Composable
fun ColorChoiceTabItem(
    text: String,
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    selected: Boolean,
) {

  val colors = if (selected) TabColors.SelectedColors else TabColors.UnselectedColors
  Surface(
      color = colors.backgroundColor,
      shape = shape,
      border = BorderStroke(color = colors.textColor, width = 4.dp),
      modifier = modifier.size(158.dp, 138.dp)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.selectable(selected, onClick = onClick).clickable { onClick }) {
      Image(
          painter = ChessIcons.BlackBishop,
          colorFilter = ColorFilter.tint(colors.textColor),
          contentDescription = null,
          modifier = modifier)
      Spacer(modifier.width(5.33.dp))
      Text(
          text = text,
          color = colors.textColor,
          style = MaterialTheme.typography.h6,
          modifier = modifier)
    }
  }
}

/* TODO: Create a dashed border modifier */
private fun Modifier.strokeDashedBorder(path: Path, color: Color, width: Dp): Modifier =
    this.drawBehind {
      drawPath(
          path,
          color = color,
          style =
              Stroke(
                  width = width.toPx(),
                  pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f))))
    }
