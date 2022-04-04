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
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState.*

/**
 * Composable for choosing a [ColorChoice]
 * @param colorChoice default or currently chosen color
 * @param onSelectColor call back when a color is selected
 * @param modifier [Modifier] for this composable
 */
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
        onClick = { onSelectColor(ColorChoice.White) },
        selected = colorChoice == ColorChoice.White,
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        modifier = Modifier.fillMaxWidth(0.5f))
    ColorChoiceTabItem(
        text = strings.prepareGameBlackColor,
        onClick = { onSelectColor(ColorChoice.Black) },
        selected = colorChoice == ColorChoice.Black,
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        modifier = Modifier.fillMaxWidth())
  }
}

/**
 * Component for a tab color choice item in [ColorChoiceBar] the component changes color given its
 * selection state. Showcases a dashed border when it's selected
 *
 * @param text text to display for this tab item
 * @param onClick triggered call back when the tab item is actioned
 * @param modifier [Modifier] for this composable
 * @param selected indicates if the tab item is currently selected
 * @param colors used [ColorChoiceColors] for the tab item depending on its selection states
 * @param borderWidth width for dashed border
 * @param shape shape of the tab item
 */
@Composable
fun ColorChoiceTabItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    colors: ColorChoiceColors = DefaultColorChoiceColors,
    borderWidth: Dp = 2.dp,
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
          modifier.let { if (selected) it.dashedBorder(borderWidth, borderColor, shape) else it }) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.selectable(selected, onClick = onClick).clickable { onClick() }.padding(16.dp),
    ) {
      Icon(
          painter = ChessIcons.BlackBishop,
          contentDescription = null,
      )
      Spacer(Modifier.width(8.dp))
      Text(
          text = text,
          color = contentColor,
          style = MaterialTheme.typography.h6,
      )
    }
  }
}

/**
 * Interface for colors used to customize colors of the [ColorChoiceTabItem]
 * @property background(selected) returns color of background depending on state of
 * [ColorChoiceTabItem]
 * @property content(selected) returns color of content depending on selection state of
 * [ColorChoiceTabItem]
 * @property border(selected) returns color of border depending on selection state of
 * [ColorChoiceTabItem]
 */
interface ColorChoiceColors {
  @Composable fun background(selected: Boolean): State<Color>

  @Composable fun content(selected: Boolean): State<Color>

  @Composable fun border(selected: Boolean): State<Color>
}

/** Default implementation of [ColorChoiceColors] */
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

/**
 * Modifier extension function used to decorate border of a [ColorChoiceTabItem] when it's selected
 * @param width width of the border
 * @param color color of the border
 * @param shape shape of the dashed border
 * @param pattern frequency/pattern of the dashed border
 */
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
