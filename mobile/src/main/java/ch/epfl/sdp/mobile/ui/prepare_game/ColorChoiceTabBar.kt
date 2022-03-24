package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*

@Composable
fun ColorChoiceBar(
    colorChoice: ColorChoice,
    onSelectColor: (ColorChoice) -> Unit,
    modifier: Modifier = Modifier
) {
  val strings = LocalLocalizedStrings.current

  Row(modifier = modifier) {
    ColorChoiceTabItem(
        text = strings.prepareGameWhiteColor,
        onClick = { onSelectColor(ColorChoice.WHITE) },
        selected = colorChoice == ColorChoice.WHITE,
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
    ColorChoiceTabItem(
        text = strings.prepareGameBlackColor,
        onClick = { onSelectColor(ColorChoice.BLACK) },
        selected = colorChoice == ColorChoice.BLACK,
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
  }
}

@Composable
fun ColorChoiceTabItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    colors: ColorChoiceColors = DefaultColorChoiceColors,
    borderWidth: Dp = 4.dp,
    shape: Shape = RectangleShape,
) {
  val contentColor by colors.content(selected)
  val borderColor by colors.border(selected)
  val backgroundColor by colors.background(selected)
  Surface(
      color = backgroundColor,
      contentColor = contentColor,
      shape = shape,
      modifier =
          modifier.size(158.dp, 138.dp).let {
            if (selected) it.dashedBorder(borderWidth, borderColor, shape) else it
          }) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.selectable(selected, onClick = onClick).clickable { onClick() }) {
      Icon(
          painter = ChessIcons.BlackBishop,
          contentDescription = null,
      )
      Spacer(modifier.width(4.dp))
      Text(
          text = text,
          color = contentColor,
          style = MaterialTheme.typography.h6,
          modifier = modifier)
    }
  }
}

interface ColorChoiceColors {
  @Composable fun background(selected: Boolean): State<Color>

  @Composable fun content(selected: Boolean): State<Color>

  @Composable fun border(selected: Boolean): State<Color>
}

private object DefaultColorChoiceColors : ColorChoiceColors {
  @Composable
  override fun background(selected: Boolean): State<Color> {
    return rememberUpdatedState(if (selected) PawniesColors.Green800 else PawniesColors.Green100)
  }

  @Composable
  override fun content(selected: Boolean): State<Color> {
    return rememberUpdatedState(if (selected) PawniesColors.Orange200 else PawniesColors.Green800)
  }
  @Composable
  override fun border(selected: Boolean): State<Color> {
    return derivedStateOf { PawniesColors.Orange200 }
  }
}

/* TODO: Create a dashed border modifier */
private fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    pattern: List<Dp> = listOf(16.dp, 8.dp),
): Modifier {
  require(pattern.isNotEmpty()) { "The dashed pattern should not be empty." }
  require(pattern.size % 2 == 0) { "The dashed pattern should have an even number of items." }
  return drawWithContent {
    drawContent()
    val pathEffectPattern = pattern.map { it.toPx() }.toFloatArray()
    val outline = shape.createOutline(size, layoutDirection, this)
    drawOutline(
        outline = outline,
        color = color,
        style =
            Stroke(
                width = width.toPx(),
                pathEffect = PathEffect.dashPathEffect(pathEffectPattern),
            ),
    )
  }
}
