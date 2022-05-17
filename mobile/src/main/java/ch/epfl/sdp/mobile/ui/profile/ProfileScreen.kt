package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * Main component of the ProfileScreen that groups ProfileHeader and list of Matches.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param state state of the ProfileScreen.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <C : ChessMatch, P : PuzzleInfo> ProfileScreen(
    state: VisitedProfileScreenState<C, P>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val lazyColumnState = rememberLazyListState()
  val tabBarState = rememberProfileTabBarState(state.pastGamesCount, state.solvedPuzzlesCount)
  val targetElevation = if (lazyColumnState.firstVisibleItemIndex >= 1) 4.dp else 0.dp
  val elevation by animateDpAsState(targetElevation)

  UserScreen(
      header = { ProfileHeader(state, Modifier.padding(vertical = 16.dp)) },
      profileTabBar = {
        ProfileTabBar(
            state = tabBarState,
            modifier = Modifier.fillMaxWidth(),
            elevation = elevation,
        )
      },
      tabBarState = tabBarState,
      matches = state.matches,
      onMatchClick = state::onMatchClick,
      puzzles = state.puzzles,
      onPuzzleClick = state::onPuzzleClick,
      lazyColumnState = lazyColumnState,
      contentPadding = contentPadding,
      modifier = modifier)
}

/**
 * Composes the profile header given the profile [state]. Displays also the ProfilePicture,
 * SettingsButton, name and email of th user profile.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param state state of profile screen.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <C : ChessMatch, P : PuzzleInfo> ProfileHeader(
    state: VisitedProfileScreenState<C, P>,
    modifier: Modifier = Modifier
) {

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ProfilePicture(state)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(state.name, style = MaterialTheme.typography.h5)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
      UnfollowButton(onClick = state::onUnfollowClick)
      Spacer(Modifier.size(16.dp))
      ChallengeButton(onClick = state::onChallengeClick)
    }
  }
}

/**
 * Composes the profile picture given its [state].
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param state state of profile screen.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <C : ChessMatch, P : PuzzleInfo> ProfilePicture(
    state: VisitedProfileScreenState<C, P>,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.size(118.dp).background(state.backgroundColor, CircleShape),
      contentAlignment = Alignment.Center,
  ) { Text(state.emoji, style = MaterialTheme.typography.h3) }
}

/**
 * Composes the unfollow button.
 *
 * @param onClick callback function for the unfollow button.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun UnfollowButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  OutlinedButton(
      onClick = onClick,
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      modifier = modifier) {
    Spacer(modifier = Modifier.width(8.dp))
    Text(strings.profileUnfollow)
  }
}

/**
 * Composes the challenge button.
 *
 * @param onClick call back method for challenge button.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun ChallengeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  OutlinedButton(
      onClick = onClick,
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      colors = ButtonDefaults.buttonColors(MaterialTheme.colors.onSurface),
      modifier = modifier) {
    Spacer(modifier = Modifier.width(8.dp))
    Text(strings.profileChallenge.uppercase())
  }
}
