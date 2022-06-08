package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

/**
 * A [Modifier] which can be used to draw a border at the bottom of the composable, which fits the
 * full width of the composable.
 *
 * @param width the width of the stroke.
 * @param color the [Color] with which the stroke is drawn.
 */
private fun Modifier.borderBottom(
    width: Dp,
    color: Color,
): Modifier = drawBehind {
  val start = Offset(0f, size.height)
  val end = Offset(size.width, size.height)
  drawLine(color, start, end, width.toPx(), StrokeCap.Round)
}

/**
 * Composes the profile tabs [PastGames and Puzzles].
 * @param title primary text of the tab item.
 * @param subtitle secondary text of the tab item.
 * @param onClick callback when the tab is clicked
 * @param selected indicates if the tab is currently selected
 */
@Composable
fun ProfileTabItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = true,
) {
  val targetColor = if (selected) LocalContentColor.current else Color.Transparent
  val borderColor by animateColorAsState(targetColor)
  val targetAlpha by animateFloatAsState(if (selected) ContentAlpha.high else ContentAlpha.medium)
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier =
          modifier
              .selectable(selected, onClick = onClick)
              .clickable { onClick() }
              .padding(horizontal = 32.dp, vertical = 8.dp),
  ) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
      Text(text = title.uppercase(), style = MaterialTheme.typography.overline)
    }
    CompositionLocalProvider(LocalContentAlpha provides targetAlpha) {
      Text(
          text = subtitle,
          style = MaterialTheme.typography.h5,
          modifier = Modifier.borderBottom(2.dp, borderColor),
      )
    }
  }
}

/**
 * Interface of the ProfileTabBar state.
 *
 * @class Tab Enumerate types of Tabs.
 * @property currentTab Currently selected Tab.
 * @property pastGamesCount past games count.
 * @property puzzlesCount puzzles count.
 */
interface ProfileTabBarState {

  /** The tabs in a profile. */
  enum class Tab {
    PastGames,
    Puzzles,
  }

  /** The currently selected tab. */
  var currentTab: Tab

  /** The number o past games. */
  val pastGamesCount: Int

  /** The number of puzzles. */
  val puzzlesCount: Int
}

/**
 * Implementation of the ProfileTabBarState.
 *
 * @param pastGamesCount count of games.
 * @param puzzlesCount count of puzzles done.
 */
private class ProfileTabBarStateImpl(
    pastGamesCount: State<Int>,
    puzzlesCount: State<Int>,
) : ProfileTabBarState {
  override val pastGamesCount by pastGamesCount
  override val puzzlesCount by puzzlesCount
  override var currentTab by mutableStateOf(ProfileTabBarState.Tab.PastGames)
}

/**
 * Function used to remember the state of the ProfileTabBar.
 *
 * @param pastGamesCount count of games.
 * @param puzzlesCount count of puzzles done.
 */
@Composable
fun rememberProfileTabBarState(
    pastGamesCount: Int,
    puzzlesCount: Int,
): ProfileTabBarState {
  val pastGamesCountState = rememberUpdatedState(pastGamesCount)
  val puzzlesCountState = rememberUpdatedState(puzzlesCount)
  return remember { ProfileTabBarStateImpl(pastGamesCountState, puzzlesCountState) }
}

/**
 * Composes a ProfileTabBar from puzzles and past games tab items.
 *
 * @param state state of the profile tab.
 * @param modifier the [Modifier] for this composable.
 * @param backgroundColor of the tab bar.
 * @param elevation elevation dp of the tab bar.
 */
@Composable
fun ProfileTabBar(
    state: ProfileTabBarState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    elevation: Dp = 0.dp,
) {
  val strings = LocalLocalizedStrings.current
  val currentElevation by animateDpAsState(elevation)
  Surface(
      modifier = modifier,
      color = backgroundColor,
      elevation = currentElevation,
  ) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier,
    ) {
      ProfileTabItem(
          title = strings.profilePastGames,
          subtitle = state.pastGamesCount.toString(),
          onClick = { state.currentTab = ProfileTabBarState.Tab.PastGames },
          selected = state.currentTab == ProfileTabBarState.Tab.PastGames,
      )
      ProfileTabItem(
          title = strings.profilePuzzle,
          subtitle = state.puzzlesCount.toString(),
          onClick = { state.currentTab = ProfileTabBarState.Tab.Puzzles },
          selected = state.currentTab == ProfileTabBarState.Tab.Puzzles,
      )
    }
  }
}
