package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Delete
import ch.epfl.sdp.mobile.ui.PawniesIcons

/** The possible states for a [SwipeToUnfollowState] composable. */
private enum class SwipeToUnfollowState {

  /** The action is hidden. */
  Closed,

  /** The action is visible. */
  Open,
}

/**
 * Displays the [content] with a swipe-to-unfollow action.
 *
 * @param onUnfollowClick the callback which is called when the unfollow action is performed.
 * @param modifier the [Modifier] for this composable.
 * @param content the body to display in the composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToUnfollow(
    onUnfollowClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  val state = rememberSwipeableState(SwipeToUnfollowState.Closed)
  val swipeWidth = 72.dp

  val anchors =
      mapOf(
          0f to SwipeToUnfollowState.Closed,
          with(LocalDensity.current) { swipeWidth.toPx() } to SwipeToUnfollowState.Open,
      )

  Box(
      modifier
          .height(IntrinsicSize.Min)
          .swipeable(state = state, anchors = anchors, orientation = Horizontal)) {
    val isSwipeInProgress by remember {
      derivedStateOf {
        state.isAnimationRunning || state.progress.fraction != 1f || state.overflow.value != 0f
      }
    }
    UnfollowBackground(
        onClick = onUnfollowClick,
        modifier =
            Modifier.width(swipeWidth)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .then(
                    if (state.currentValue == SwipeToUnfollowState.Open && !isSwipeInProgress)
                        Modifier.zIndex(1f)
                    else Modifier),
    )
    Box(
        Modifier.offset { Offset(x = state.offset.value, y = 0f).round() },
    ) { content() }
  }
}

/**
 * The background of a [SwipeToUnfollowState].
 *
 * @param onClick the callback called when the background is clicked.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true iff the clicks should be handled.
 */
@Composable
private fun UnfollowBackground(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
  val background = MaterialTheme.colors.error.copy(alpha = 0.2f)
  val strings = LocalLocalizedStrings.current
  Box(
      modifier.clickable(enabled = enabled) { onClick() }.background(background),
      Alignment.CenterStart,
  ) {
    Icon(
        imageVector = PawniesIcons.Delete,
        contentDescription = strings.socialUnfollowIcon,
        tint = MaterialTheme.colors.error.copy(alpha = 0.6f),
        modifier = Modifier.padding(horizontal = 24.dp),
    )
  }
}
