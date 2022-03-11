package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import ch.epfl.sdp.mobile.ui.profile.ProfileTabBarState.Tab.PastGames
import ch.epfl.sdp.mobile.ui.profile.ProfileTabBarState.Tab.Puzzles

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
 * Composes the profile tabs [PastGames and Puzzles]
 * @param text text of the tab
 * @param num counter
 * @param onClick callback when the tab is clicked
 * @param selected indicates if the tab is currently selected
 */
@Composable
fun ProfileTabItem(
    text: String,
    num: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = true,
) {
  val targetColor = if (selected) LocalContentColor.current else Color.Transparent
  val borderColor by animateColorAsState(targetColor)
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier =
          modifier
              .selectable(selected, onClick = onClick)
              .clickable { onClick() }
              .padding(horizontal = 32.dp, vertical = 8.dp),
  ) {
    Text(text = text, style = MaterialTheme.typography.overline)
    Text(
        text = "$num",
        style = MaterialTheme.typography.h5,
        modifier = Modifier.borderBottom(2.dp, borderColor),
    )
  }
}

/**
 * Interface of the ProfileTabBar state
 * @class Tab Enumerate types of Tabs
 * @property currentTab Currently selected Tab
 * @property pastGamesCount past games count
 * @property puzzlesCount puzzles count
 */
interface ProfileTabBarState {

  enum class Tab {
    PastGames,
    Puzzles,
  }
  var currentTab: Tab
  val pastGamesCount: Int
  val puzzlesCount: Int
}

/** Implementation of the ProfileTabBarState */
private class ProfileTabBarStateImpl(
    pastGamesCount: State<Int>,
    puzzlesCount: State<Int>,
) : ProfileTabBarState {
  override val pastGamesCount by pastGamesCount
  override val puzzlesCount by puzzlesCount
  override var currentTab by mutableStateOf(PastGames)
}

/** Function used to remember the state of the ProfileTabBar */
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
 * Composes a ProfileTabBar from puzzles and past games tab items
 * @param state state of the profile tab
 * @param modifier the [Modifier] for this composable.
 * @param backgroundColor of the tab bar
 * @param elevation elevation dp of the tab bar
 */
@Composable
fun ProfileTabBar(
    state: ProfileTabBarState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    elevation: Dp = 0.dp,
) {
  val strings = LocalLocalizedStrings.current
  Surface(
      modifier = modifier,
      color = backgroundColor,
      elevation = elevation,
  ) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier,
    ) {
      ProfileTabItem(
          text = strings.profilePastGames,
          num = state.pastGamesCount,
          onClick = { state.currentTab = PastGames },
          selected = state.currentTab == PastGames,
      )
      ProfileTabItem(
          text = strings.profilePuzzle,
          num = state.puzzlesCount,
          onClick = { state.currentTab = Puzzles },
          selected = state.currentTab == Puzzles,
      )
    }
  }
}
