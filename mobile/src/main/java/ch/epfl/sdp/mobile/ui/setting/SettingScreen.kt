package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Edit
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.Settings
import ch.epfl.sdp.mobile.ui.profile.ProfileTabBar
import ch.epfl.sdp.mobile.ui.profile.UserScreen
import ch.epfl.sdp.mobile.ui.profile.rememberProfileTabBarState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * Main component of the ProfileScreen that groups ProfileHeader and list of Matches.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param state state of the ProfileScreen.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <C : ChessMatch, P : PuzzleInfo> SettingsScreen(
    state: SettingScreenState<C, P>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val lazyColumnState = rememberLazyListState()
  val tabBarState = rememberProfileTabBarState(state.pastGamesCount, state.solvedPuzzlesCount)
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
        ProfileTabBar(
            state = tabBarState,
            modifier = Modifier.fillMaxWidth(),
            elevation = elevation,
        )
      },
      matches = state.matches,
      lazyColumnState = lazyColumnState,
      modifier = modifier.fillMaxSize(),
      contentPadding = contentPadding,
      onMatchClick = state::onMatchClick)
}

/**
 * Composes the settings header given the profile [SettingScreenState]. Displays also the
 * ProfilePicture, SettingsButton, name and email of the user's profile.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param state state of the profile screen.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <C : ChessMatch, P : PuzzleInfo> SettingHeader(state: SettingScreenState<C, P>, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    SettingPicture(state)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically) {
        Text(state.name, style = MaterialTheme.typography.h5)
        IconButton(onClick = state::onEditProfileNameClick) {
          Image(
              PawniesIcons.Edit,
              contentDescription = strings.profileEditNameIcon,
              modifier = Modifier.size(24.dp))
        }
      }
      Text(state.email, style = MaterialTheme.typography.subtitle2)
    }
    SettingsButton(onClick = state::onEditProfileImageClick)
  }
}

/**
 * Composes the settings picture given its [state].
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param state state of the setting screen.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <C : ChessMatch, P : PuzzleInfo> SettingPicture(
    state: SettingScreenState<C, P>,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  Box(
      modifier = modifier.size(118.dp).background(state.backgroundColor, CircleShape),
      contentAlignment = Alignment.Center,
  ) {
    Text(state.emoji, style = MaterialTheme.typography.h3)
    IconButton(
        onClick = state::onEditProfileImageClick,
        modifier =
            Modifier.align(Alignment.BottomEnd)
                .shadow(2.dp, CircleShape)
                .background(MaterialTheme.colors.surface, CircleShape)
                .border(2.dp, MaterialTheme.colors.primary, CircleShape)
                .size(40.dp),
    ) { Image(PawniesIcons.Edit, strings.profileEditImageIcon) }
  }
}

/**
 * Composes the settings button.
 *
 * @param onClick callback method for the settings button.
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
    Icon(PawniesIcons.Settings, null)
    Spacer(modifier = Modifier.width(8.dp))
    Text(strings.profileSettings)
  }
}
