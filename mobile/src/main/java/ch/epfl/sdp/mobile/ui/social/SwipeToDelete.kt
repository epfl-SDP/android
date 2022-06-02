
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.SwipeableDefaults.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import ch.epfl.sdp.mobile.ui.Delete
import ch.epfl.sdp.mobile.ui.PawniesIcons
import traak.android.ui.TaskDelete
import traak.android.ui.TraakIcons
import traak.android.ui.tasks.SwipeToDeleteState.Closed
import traak.android.ui.tasks.SwipeToDeleteState.Open

/** The possible states for a [SwipeToDelete] composable. */
private enum class SwipeToDeleteState {

  /** The action is hidden. */
  Closed,

  /** The action is visible. */
  Open,
}

/**
 * Displays the [content] with a swipe-to-delete action.
 *
 * @param onDeleteClick the callback which is called when the deletion action is performed.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true iff the swipe-to-delete action should be enabled.
 * @param content the body to display in the composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDelete(
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  content: @Composable () -> Unit,
) {
  val state =
    rememberSaveable(
      saver = SwipeableState.Saver(AnimationSpec) { true },
    ) { SwipeableState(SwipeToDeleteState.Closed) }
  val anchors =
    mapOf(
      0f to SwipeToDeleteState.Closed,
      with(LocalDensity.current) { 72.dp.toPx() } to SwipeToDeleteState.Open,
    )

  Box(
    modifier.swipeable(
      state = state,
      anchors = anchors,
      orientation = Horizontal,
      enabled = enabled,
    ),
  ) {
    DeleteBackground(
      onClick = onDeleteClick,
      enabled = enabled,
      modifier = Modifier.matchParentSize(),
    )
    Box(
      Modifier.offset { Offset(x = state.offset.value, y = 0f).round() },
    ) { content() }
  }
}

/**
 * The background of a [SwipeToDelete].
 *
 * @param onClick the callback called when the background is clicked.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true iff the clicks should be handled.
 */
@Composable
private fun DeleteBackground(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val background = MaterialTheme.colors.error.copy(alpha = 0.2f)
  Box(
    modifier.clickable(enabled = enabled) { onClick() }.background(background),
    Alignment.CenterStart,
  ) {
    Icon(
      imageVector = PawniesIcons.Delete,
      contentDescription = null,
      tint = MaterialTheme.colors.error.copy(alpha = 0.6f),
      modifier = Modifier.padding(horizontal = 24.dp),
    )
  }
}

