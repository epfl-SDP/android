package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesTheme

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
      items(state.players) { friend ->
        SocialCard(
            person = friend,
            {
              OutlinedButton(
                  onClick = { /*TODO*/},
                  shape = RoundedCornerShape(24.dp),
              ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = LocalLocalizedStrings.current.socialPerformPlay.uppercase(),
                )
              }
            })
      }
    }
  }
}

@Preview
@Composable
fun test() {
  val p =
      object : Person {
        override val backgroundColor: ProfileColor
          get() = ProfileColor.Pink
        override val name: String
          get() = "Toto"
        override val emoji: String
          get() = ";3"
      }

  val state =
      object : FollowingState {
        override val players: List<Person>
          get() = List(10) { p }
      }

  PawniesTheme { SocialScreen(state = state) }
}
