package ch.epfl.sdp.mobile.ui.play

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import ch.epfl.sdp.mobile.ui.FadeEnterTransition
import ch.epfl.sdp.mobile.ui.FadeExitTransition
import ch.epfl.sdp.mobile.ui.PawniesColors.Beige050
import ch.epfl.sdp.mobile.ui.PawniesColors.Green100
import ch.epfl.sdp.mobile.ui.PawniesColors.Green800

/** The state of an [ExpandableFloatingActionButton] */
interface ExpandableFloatingActionButtonState {

  /** True iff the button should be currently expanded. */
  var expanded: Boolean
}

/**
 * An interface representing the colors of an [ExpandableFloatingActionButton]. The colors may
 * change depending on whether the button is currently expanded or not.
 */
interface ExpandableFloatingActionButtonColors {

  /**
   * Returns a state with the color for the button background.
   *
   * @param expanded true if the button is currently expanded.
   */
  @Composable fun background(expanded: Boolean): State<Color>

  /**
   * Returns a state with the color for the button border.
   *
   * @param expanded true if the button is currently expanded.
   */
  @Composable fun border(expanded: Boolean): State<Color>

  /**
   * Returns a state with the color for the button content color.
   *
   * @param expanded true if the button is currently expanded.
   */
  @Composable fun contentColor(expanded: Boolean): State<Color>
}

/**
 * A composable which displays a floating action which may be expanded.
 *
 * @param expandedContent the content to display when expanded. Usually a list of
 * [ExpandableFloatingActionButtonItem].
 * @param modifier the [Modifier] for this composable.
 * @param state the backing state for this button.
 * @param colors the colors for this button.
 * @param elevation the elevation for this button.
 * @param interactionSource the [MutableInteractionSource] to keep track of inputs.
 * @param expandedCornerSize the size of the rounded corners when the button is expanded.
 * @param collapsedContent the content to display when collapsed.
 */
@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun ExpandableFloatingActionButton(
    expandedContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    state: ExpandableFloatingActionButtonState = rememberExpandableFloatingActionButtonState(),
    colors: ExpandableFloatingActionButtonColors = DefaultExpandableFloatingActionButtonColors,
    elevation: ButtonElevation = ButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    expandedCornerSize: Dp = 8.dp,
    collapsedContent: @Composable RowScope.() -> Unit,
) {
  val color by colors.background(state.expanded)
  val border by colors.border(state.expanded)
  val contentColor by colors.contentColor(state.expanded)
  val progress = animateFloatAsState(if (state.expanded) 1f else 0f)
  val corner = remember(expandedCornerSize) { ProgressiveCornerSize(expandedCornerSize, progress) }

  Surface(
      color = color,
      contentColor = contentColor,
      border = BorderStroke(2.dp, border),
      elevation = elevation.elevation(enabled = true, interactionSource).value,
      shape = RoundedCornerShape(corner),
      modifier = modifier,
  ) {
    AnimatedContent(
        targetState = state.expanded,
        contentAlignment = Alignment.Center,
        transitionSpec = { FadeEnterTransition with FadeExitTransition },
    ) { expanded ->
      ProvideTextStyle(MaterialTheme.typography.button) {
        if (expanded) ExpandedContent(content = expandedContent)
        else
            CollapsedContent(
                modifier =
                    Modifier.clickable { state.expanded = true }
                        .padding(vertical = 20.dp, horizontal = 24.dp),
                content = collapsedContent,
            )
      }
    }
  }
}

/**
 * An item that may be displayed within an [ExpandableFloatingActionButton] if it's currently
 * expanded. These items are clickable and essentially work like buttons.
 *
 * @param onClick the callback which will be called when the button is clicked.
 * @param icon the slot for the action icon.
 * @param modifier the [Modifier] for this composable.
 * @param text the slot for the action text.
 */
@Composable
fun ExpandableFloatingActionButtonItem(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
) {
  Row(
      modifier = modifier.clickable { onClick() }.padding(vertical = 20.dp, horizontal = 24.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // This box is required for intrinsic measurements to work properly, since icons don't quite
    // support them properly yet.
    Box(Modifier.size(24.dp), Alignment.Center) { icon() }
    Spacer(Modifier.width(16.dp))
    text()
  }
}

// Content wrappers with proper Arrangement and Alignment.

@Composable
private fun CollapsedContent(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) = Row(modifier, Arrangement.Start, Alignment.CenterVertically, content)

@Composable
private fun ExpandedContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier.width(IntrinsicSize.Max), Arrangement.Top, Alignment.Start, content)

// State management.

/**
 * Creates and remembers an [ExpandableFloatingActionButtonState].
 *
 * @param initiallyExpanded true if the floating action button should initially be expanded.
 */
@Composable
fun rememberExpandableFloatingActionButtonState(
    initiallyExpanded: Boolean = false,
): ExpandableFloatingActionButtonState {
  val expanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
  return SnapshotExpandableFloatingActionButtonState(expanded)
}

/**
 * An implementation of an [ExpandableFloatingActionButtonState] that delegates to a backing
 * [MutableState].
 *
 * @param state the [MutableState] to which the expanded state is delegated.
 */
private class SnapshotExpandableFloatingActionButtonState(
    state: MutableState<Boolean>,
) : ExpandableFloatingActionButtonState {
  override var expanded by state
}

// Configuration.

/**
 * The default [ExpandableFloatingActionButtonColors], which default to some branded colors from the
 * Pawnies theme.
 */
private object DefaultExpandableFloatingActionButtonColors : ExpandableFloatingActionButtonColors {

  @Composable
  override fun background(expanded: Boolean): State<Color> {
    return animateColorAsState(if (expanded) Beige050 else Green800)
  }

  @Composable
  override fun border(expanded: Boolean): State<Color> {
    return animateColorAsState(if (expanded) Green100 else Green100.copy(alpha = 0f))
  }

  @Composable
  override fun contentColor(expanded: Boolean): State<Color> {
    return animateColorAsState(if (expanded) Green800 else Green100)
  }
}

/**
 * An implementation of [CornerSize] which progresses between a [CircleShape] and a standard
 * [CornerSize] with fixed [Dp].
 *
 * @param size the fixed [Dp] when the progress has value 1f.
 * @param progress the current progress, between 0f (circle shape) and 1f (fixed size).
 */
private class ProgressiveCornerSize(
    private val size: Dp,
    private val progress: State<Float>,
) : CornerSize {
  override fun toPx(shapeSize: Size, density: Density): Float {
    val currentProgress by progress
    val clampedProgress = currentProgress.coerceIn(0f, 1f)
    val half = shapeSize.minDimension / 2f
    val fixed = with(density) { size.toPx() }
    return lerp(half, fixed, clampedProgress)
  }
}
