package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.SectionSocial
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.social.Loss
import ch.epfl.sdp.mobile.ui.social.MatchResult
import ch.epfl.sdp.mobile.ui.social.Tie
import ch.epfl.sdp.mobile.ui.social.Win

/**
 * Main component of the ProfileScreen that groups ProfileHeader and list of Matches
 * @param state state of the ProfileScreen
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    state: ProfileScreenState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val lazyColumnState = rememberLazyListState()
  val targetElevation = if (lazyColumnState.firstVisibleItemIndex >= 1) 4.dp else 0.dp
  val elevation by animateDpAsState(targetElevation)
  LazyColumn(
      state = lazyColumnState,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
  ) {
    item { ProfileHeader(state, Modifier.padding(vertical = 16.dp)) }
    stickyHeader {
      ProfileTabBar(
          pastGamesCount = state.pastGamesCount,
          modifier = Modifier.fillMaxWidth(),
          elevation = elevation,
      )
    }
    items(state.matches) { match ->
      val title = strings.profileMatchTitle(match.adversary)
      val subtitle = chooseSubtitle(strings, match.matchResult, match.numberOfMoves)
      Match(title, subtitle, PawniesIcons.SectionSocial)
    }
  }
}

/**
 * Chooses a subtitle given a [MatchResult] and number of moves [nMoves] of the match
 * @param matchResult result of the match
 * @param nMoves number of moves
 */
private fun chooseSubtitle(
    strings: LocalizedStrings,
    matchResult: MatchResult,
    nMoves: Int
): String {
  val text =
      when (matchResult) {
        Tie -> strings.profileTieInfo
        is Loss ->
            when (matchResult.reason) {
              MatchResult.Reason.CHECKMATE -> strings.profileLostByCheckmate
              MatchResult.Reason.FORFEIT -> strings.profileLostByForfeit
            }
        is Win ->
            when (matchResult.reason) {
              MatchResult.Reason.CHECKMATE -> strings.profileWonByCheckmate
              MatchResult.Reason.FORFEIT -> strings.profileWonByForfeit
            }
      }
  return text(nMoves)
}

/**
 * Composes the profile header given the profile [state]. Displays also the ProfilePicture,
 * SettingsButton, name and email of th user profile
 * @param state state of profile screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun ProfileHeader(state: ProfileScreenState, modifier: Modifier = Modifier) {

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ProfilePicture(state)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(state.name, style = MaterialTheme.typography.h5)
      Text(state.email, style = MaterialTheme.typography.subtitle2)
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
 * Composes the profile picture given its [state]
 * @param state state of profile screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun ProfilePicture(
    state: ProfileScreenState,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.size(118.dp).background(state.backgroundColor.toColor(), CircleShape),
      contentAlignment = Alignment.Center,
  ) { Text(state.emoji, style = MaterialTheme.typography.h3) }
}

/**
 * Composes the unfollow button
 * @param onClick call back method for unfollow button
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
 * Composes the challenge button
 * @param onClick call back method for challenge button
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

/**
 * Composes a Match log using a match [title], [subtitle] and an [icon]
 * @param title match title
 * @param subtitle match subtitle info
 * @param icon match icon
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Match(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
  ListItem(
      modifier = modifier,
      icon = { Icon(icon, null) },
      text = { Text(title) },
      secondaryText = { Text(subtitle) },
  )
}

/**
 * Composes a Profile from puzzles and past games tab items
 * @param pastGamesCount total of the previous games
 * @param modifier the [Modifier] for this composable.
 * @param backgroundColor of the tab bar
 * @param elevation elevation dp of the tab bar
 */
@Composable
fun ProfileTabBar(
    pastGamesCount: Int,
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
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = modifier.padding(horizontal = 32.dp, vertical = 8.dp),
      ) {
        Text(text = strings.profilePastGames, style = MaterialTheme.typography.overline)
        Text(
            text = "$pastGamesCount",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.borderBottom(2.dp, Color.Transparent),
        )
      }
    }
  }
}

private fun Modifier.borderBottom(
    width: Dp,
    color: Color,
): Modifier = drawBehind {
  val start = Offset(0f, size.height)
  val end = Offset(size.width, size.height)
  drawLine(color, start, end, width.toPx(), StrokeCap.Round)
}
