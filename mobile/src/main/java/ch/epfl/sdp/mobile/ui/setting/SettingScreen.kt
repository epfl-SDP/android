package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.SectionSocial
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.profile.*
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
fun SettingsScreen(
    state: SettingScreenState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val lazyColumnState = rememberLazyListState()
  val tabBarState = rememberSettingTabBarState(state.pastGamesCount, state.puzzlesCount)
  val targetElevation = if (lazyColumnState.firstVisibleItemIndex >= 1) 4.dp else 0.dp
  val elevation by animateDpAsState(targetElevation)
  LazyColumn(
      state = lazyColumnState,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
  ) {
    item { SettingHeader(state, Modifier.padding(vertical = 16.dp)) }
    stickyHeader {
      SettingTabBar(
          state = tabBarState,
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
 * Composes the settings header given the profile [state]. Displays also the ProfilePicture,
 * SettingsButton, name and email of th user profile
 * @param state state of profile screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun SettingHeader(state: SettingScreenState, modifier: Modifier = Modifier) {

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    SettingPicture(state)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(state.name, style = MaterialTheme.typography.h5)
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
  Box(
      modifier = modifier.size(118.dp).background(state.backgroundColor.toColor(), CircleShape),
      contentAlignment = Alignment.Center,
  ) {
    Text(state.emoji, style = MaterialTheme.typography.h3)
    IconButton(
        onClick = state::onEditClick,
        modifier =
            Modifier.align(Alignment.BottomEnd)
                .shadow(2.dp, CircleShape)
                .background(MaterialTheme.colors.surface, CircleShape)
                .border(2.dp, MaterialTheme.colors.primary, CircleShape)
                .size(40.dp),
    ) { Icon(Icons.Default.Edit, "Edit profile icon") }
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
