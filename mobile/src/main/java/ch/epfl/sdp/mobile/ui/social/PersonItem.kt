package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import ch.epfl.sdp.mobile.ui.Delete
import ch.epfl.sdp.mobile.ui.PawniesColors.Beige050
import ch.epfl.sdp.mobile.ui.PawniesIcons

private enum class SwipeToDeleteState {

  /** The action is hidden. */
  Closed,

  /** The action is visible. */
  Open,
}

/**
 * This list item is used to display player information in the Social screen.
 *
 * @param person The [Person] contains the information that need to be displayed
 * @param trailingAction Define the trailing action in the card
 * @param modifier the [Modifier] for the composable
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonItem(
    person: Person,
    swipeable: Boolean = false,
    onShowProfileCLick: () -> Unit,
    onUnfollow: () -> Unit = {},
    modifier: Modifier = Modifier,
    trailingAction: @Composable () -> Unit = {}
) {
  val state = rememberSwipeableState(SwipeToDeleteState.Closed)

  val anchors =
      mapOf(
          0f to SwipeToDeleteState.Closed,
          with(LocalDensity.current) { 72.dp.toPx() } to
              SwipeToDeleteState.Open) // Maps anchor points (in px) to states

  Box(
      modifier.swipeable(
          state = state,
          anchors = anchors,
          orientation = Orientation.Horizontal,
          enabled = swipeable),
  ) {
    UnfollowBackground(
        onClick = onUnfollow,
        modifier = Modifier.matchParentSize(),
    )
    Box(
        modifier =
            Modifier.offset {
              Offset(x = state.offset.value, y = 0f).round()
            },
    ) {
      ListItem(
          modifier = Modifier.background(Beige050).clickable { onShowProfileCLick() },
          icon = {
            Box(
                modifier =
                    Modifier.size(40.dp).clip(CircleShape).background(person.backgroundColor)) {
              Text(person.emoji, modifier = Modifier.align(Alignment.Center))
            }
          },
          text = {
            Text(
                text = person.name,
                color = MaterialTheme.colors.primaryVariant,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
          },
          trailing = trailingAction,
      )
    }
  }
}

/**
 * The background of a [PersonItem].
 *
 * @param onClick the callback called when the background is clicked.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true iff the clicks should be handled.
 */
@Composable
private fun UnfollowBackground(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val background = MaterialTheme.colors.error.copy(alpha = 0.2f)
  Box(
      modifier.clickable { onClick() }.background(background),
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
