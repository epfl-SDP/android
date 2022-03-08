package ch.epfl.sdp.mobile.ui.features.profile

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
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.branding.PawniesTheme
import ch.epfl.sdp.mobile.ui.features.ProfileColor
import ch.epfl.sdp.mobile.ui.features.social.ChessMatch
import ch.epfl.sdp.mobile.ui.features.social.MatchResult
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings

val state =
    object : ProfileState {
      override val email: String = "badrtaddist1@gmail.com"
      override val numberOfGames: Int = 10
      override val numberOfPuzzles: Int = 10
      override val matches: List<ChessMatch> =
          List(20) { ChessMatch("Konor($it)", MatchResult.WIN, MatchResult.Reason.CHECKMATE, 27) }

      override fun onSettingsClick() {}
      override fun onEditClick() {}
      override val backgroundColor: ProfileColor = ProfileColor.Pink
      override val name: String = "Badr Taddist"
      override val emoji: String = "😊"
    }

@Preview
@Composable
fun preview() {
  //  SettingsButton(state::onSettingsClick)
  // ProfileHeader(state = state)
  //  GamesInfo(numPastGames = 42, numPuzzles = 10)
  PawniesTheme { Scaffold { ProfileScreen(state = state) } }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val tabBarState = rememberProfileTabBarState(state.numberOfGames, state.numberOfPuzzles)
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
          state = tabBarState,
          numPastGames = state.numberOfGames,
          numPuzzles = state.numberOfPuzzles,
          modifier = Modifier.fillMaxWidth(),
          elevation = elevation,
      )
    }
    items(state.matches) { match ->
      val title = strings.profileMatchTitle(match.adv)
      val subtitle = strings.profileMatchInfo(match.matchResult, match.cause, match.numberOfMoves)
      Match(title, subtitle)
    }
  }
}

@Composable
fun ProfileHeader(state: ProfileState, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ProfilePicture(state)
    Column {
      Text(state.name, style = MaterialTheme.typography.h5)
      Text(state.email, style = MaterialTheme.typography.subtitle2)
    }
    SettingsButton(onClick = state::onSettingsClick)
  }
}

@Composable
fun ProfilePicture(
    state: ProfileState,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier =
          modifier.size(118.dp).background(state.backgroundColor.getColorForProfile(), CircleShape),
      contentAlignment = Alignment.Center,
  ) {
    Text(state.emoji, style = MaterialTheme.typography.h3)
    IconButton(
        onClick = state::onEditClick,
        modifier =
            Modifier.align(Alignment.BottomEnd)
                .background(MaterialTheme.colors.surface, CircleShape)
                .border(2.dp, MaterialTheme.colors.primary, CircleShape)
                .shadow(2.dp, CircleShape)
                .size(40.dp),
    ) { Icon(Icons.Default.Edit, "Edit profile icon") }
  }
}

@Composable
fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
      icon = { Icon(icon, "Match icon") },
      text = { Text(title) },
      secondaryText = { Text(subtitle) },
  )
}
