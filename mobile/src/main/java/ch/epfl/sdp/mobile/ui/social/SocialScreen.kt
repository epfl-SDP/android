package ch.epfl.sdp.mobile.ui.social

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.toColor

/**
 * This screen displays all registered users of the app
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
                Modifier.size(40.dp).clip(CircleShape).background(person.backgroundColor.toColor()),
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
