package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
          listOf(ChessMatch("Konor", MatchResult.WIN, MatchResult.Reason.CHECKMATE, 27))
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
  GamesInfo(numPastGames = 42, numPuzzles = 10)
}

@Composable fun ProfileScreen(state: ProfileState, modifier: Modifier = Modifier) {}

@Composable
fun ProfileHeader(state: ProfileState, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    ProfilePicture(state)
    Spacer(Modifier.height(16.dp))
    Text(state.name, style = MaterialTheme.typography.h5)
    Text(state.email, style = MaterialTheme.typography.subtitle2)
    Spacer(Modifier.height(16.dp))
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
fun SettingsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  Button(
      onClick = onClick,
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      modifier = modifier) {
    Icon(Icons.Default.Settings, null)
    Spacer(modifier = Modifier.width(8.dp))
    Text(strings.profileSettings)
  }
}

@Composable
fun infoComponent(text: String, num: Int, modifier: Modifier = Modifier) {
  // Past games component
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = modifier) {
    Text(text = text, modifier = modifier)
    Text(
        text = "$num",
        style = TextStyle(textDecoration = TextDecoration.Underline),
        modifier = modifier)
  }
}

@Composable
fun GamesInfo(numPastGames: Int, numPuzzles: Int, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier) {
    infoComponent(
        text = strings.profilePastGames,
        num = numPastGames,
        modifier = modifier.padding(horizontal = 32.dp))
    infoComponent(
        text = strings.profilePuzzle,
        num = numPuzzles,
        modifier = modifier.padding(horizontal = 32.dp))
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
      icon = { Image(icon, "Match icon") },
      text = { Text(title) },
      secondaryText = { Text(subtitle) },
  )
}
