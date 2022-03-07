package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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
  val friendList = remember { state.players }
  Column {
    Text(
        text = strings.SocialFollowingTitle,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(16.dp))
    LazyColumn(modifier.testTag("friendList")) {
      items(friendList) { friend -> FriendCard(person = friend) }
    }
  }
}

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

  Card(modifier, backgroundColor = MaterialTheme.colors.background) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier.testTag("ProfileBg")
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getBackgroundRGB(person.backgroundColor))) {
          Text(person.emoji, modifier = Modifier.align(Alignment.Center))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
          Text(
              person.name,
              modifier = Modifier.padding(8.dp),
              color = MaterialTheme.colors.secondary,
              style = MaterialTheme.typography.subtitle1)

          OutlinedButton(
              onClick = { /*TODO*/},
              shape = RoundedCornerShape(24.dp),
              border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)) {
            Text(text = LocalLocalizedStrings.current.SocialPerformPlay)
          }
        }
      }
    }
  }
}
