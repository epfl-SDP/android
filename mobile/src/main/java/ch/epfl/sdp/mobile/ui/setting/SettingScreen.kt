package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.ui.profile.SettingTabBar
import ch.epfl.sdp.mobile.ui.profile.UserScreen
import ch.epfl.sdp.mobile.ui.profile.rememberSettingTabBarState

/**
 * Main component of the ProfileScreen that groups ProfileHeader and list of Matches
 * @param state state of the ProfileScreen
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(
    state: SettingScreenState,
    modifier: Modifier = Modifier,
) {
  val lazyColumnState = rememberLazyListState()
  val tabBarState = rememberSettingTabBarState(state.pastGamesCount, state.puzzlesCount)
  val targetElevation = if (lazyColumnState.firstVisibleItemIndex >= 1) 4.dp else 0.dp
  val elevation by animateDpAsState(targetElevation)

  UserScreen(
      header = {
        SettingHeader(
            state = state,
            modifier = Modifier.padding(vertical = 16.dp),
        )
      },
      profileTabBar = {
        SettingTabBar(
            state = tabBarState,
            modifier = Modifier.fillMaxWidth(),
            elevation = elevation,
        )
      },
      matches = state.matches,
      onMatchClick = {},
      lazyColumnState = lazyColumnState,
      modifier = modifier)
}

/**
 * Composes the settings header given the profile [SettingScreenState]. Displays also the
 * ProfilePicture, SettingsButton, name and email of the user's profile
 * @param state state of profile screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun SettingHeader(state: SettingScreenState, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    SettingPicture(state)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(horizontalArrangement = Arrangement.Center) {
        Text(state.name, style = MaterialTheme.typography.h5)
        IconButton(onClick = state::openEditProfileName, modifier = Modifier.size(35.dp).testTag("editProfileName")) {
          Icon(Icons.Default.Edit, strings.profileEditIcon)
        }
      }
      Text(state.email, style = MaterialTheme.typography.subtitle2)
    }
    SettingsButton(onClick = state::onSettingsClick)
  }
}

/**
 * Composes the settings picture given its [state]
 * @param state state of setting screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun SettingPicture(
    state: SettingScreenState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  Box(
      modifier = modifier.size(118.dp).background(state.backgroundColor.toColor(), CircleShape),
      contentAlignment = Alignment.Center,
  ) {
    Text(state.emoji, style = MaterialTheme.typography.h3)
    IconButton(
        onClick = state::onSettingsClick,
        modifier =
            Modifier.align(Alignment.BottomEnd)
                .shadow(2.dp, CircleShape)
                .background(MaterialTheme.colors.surface, CircleShape)
                .border(2.dp, MaterialTheme.colors.primary, CircleShape)
                .size(40.dp),
    ) { Icon(Icons.Default.Edit, strings.profileEditIcon) }
  }
}

/**
 * Composes the settings button
 * @param onClick call back method for settings button
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun SettingsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  OutlinedButton(
      onClick = onClick,
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      modifier = modifier) {
    Icon(Icons.Default.Settings, null)
    Spacer(modifier = Modifier.width(8.dp))
    Text(strings.profileSettings)
  }
}
