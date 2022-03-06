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
import androidx.compose.ui.tooling.preview.Preview
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
fun SocialScreen(
  state: FollowingState,
  modifier: Modifier = Modifier
) {

  val strings = LocalLocalizedStrings.current
  val friendList = remember { state.players }
  Column {
    Text(
      text = strings.SocialFollowingTitle,
      color = MaterialTheme.colors.primary,
      style = MaterialTheme.typography.h4,
      modifier = Modifier.padding(16.dp)
    )
    LazyColumn(modifier) {
      items(friendList) { friend ->
        FriendCard(person = friend)
      }
    }
  }
}


@Composable
fun FriendCard(person: Person, modifier: Modifier = Modifier) {
  val bgColor = when (person.backgroundColor) {
    ProfileColor.Pink -> Color.Magenta
    else -> Color.Black
  }

  Card(modifier, backgroundColor = MaterialTheme.colors.background) {
    Column(
      Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        Modifier
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bgColor)
        ) {
          Text(person.emoji, modifier = Modifier.align(Alignment.Center))
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            person.name,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.subtitle1
          )

          OutlinedButton(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
          ) {
            Text(text = LocalLocalizedStrings.current.SocialPerformPlay)
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun previewSocialScreen() {
  val p = object : Person {
    override val backgroundColor: ProfileColor
      get() = ProfileColor.Pink
    override val name: String
      get() = "Toto"
    override val emoji: String
      get() = TODO("Not yet implemented")
  }

  val state = object : FollowingState {
    override val players: List<Person>
      get() = List(10) { p }
  }

  SocialScreen(state = state)
}
