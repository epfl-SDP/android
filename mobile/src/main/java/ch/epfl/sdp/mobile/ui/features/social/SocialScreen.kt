package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.branding.PawniesTheme
import ch.epfl.sdp.mobile.ui.features.ProfileColor
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings

/**
 * This screen display all register user of the app
 *
 * @param state the [FollowingState], manage the composable contents
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun SocialScreen(state: FollowingState, modifier: Modifier = Modifier) {

  val strings = LocalLocalizedStrings.current
  Column(modifier) {
    Text(
        text = strings.socialFollowingTitle,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp))
    LazyColumn(Modifier.testTag("friendList")) {
      items(state.players) { friend -> FriendCard(person = friend) }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendCard(person: Person, modifier: Modifier = Modifier) {
  /**
   * This function transforme the user profile color into a [Color] that can be used in a composable
   *
   * TODO : Need to define how to stock the color in the class [Person]
   *
   * @param color the profile image background defined by the user
   */
  fun getBackgroundRGB(color: ProfileColor): Color {
    if (color == ProfileColor.Pink) {
      return Color.Magenta
    }
    return Color.Black
  }

  ListItem(
      modifier = modifier.padding(vertical = 8.dp),
      text = {
        Text(
            person.name,
            color = MaterialTheme.colors.primaryVariant,
            style = MaterialTheme.typography.subtitle1)
      },
      icon = {
        Box(
            modifier =
                Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(getBackgroundRGB(person.backgroundColor)),
        ) { Text(person.emoji, modifier = Modifier.align(Alignment.Center)) }
      },
      trailing = {
        OutlinedButton(
            onClick = { /*TODO*/},
            shape = RoundedCornerShape(24.dp),
        ) {
          Text(
              modifier = Modifier.padding(horizontal = 8.dp),
              text = LocalLocalizedStrings.current.socialPerformPlay.uppercase())
        }
      })
}
