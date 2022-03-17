package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.Search
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.*

/**
 * This screen display all register user of the app
 *
 * @param state the [SocialScreenState], manage the composable contents
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun SocialScreen(state: SocialScreenState, modifier: Modifier = Modifier) {

  val transition = updateTransition(state.mode, "Social state")

  Scaffold(
      modifier = modifier,
      topBar = {
        Surface(elevation = 0.dp) {
          SearchField(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              value = state.input,
              onValueChange = { state.input = it },
          )
        }
      },
      content = {
        transition.AnimatedContent { target ->
          when (target) {
            Following -> FollowList(state.players)
            Searching ->
                if (state.input.isEmpty()) EmptySearch()
                else SearchResultList(players = state.players)
          }
        }
      },
  )
}

/**
 * Display the list of followed player
 * @param players A list of [Person] that need to be displayed
 * @param modifier modifier the [Modifier] for the composable
 */
@Composable
fun FollowList(players: List<Person>, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  LazyColumn(
      modifier = modifier.testTag("friendList"),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item {
      Text(
          text = strings.socialFollowingTitle,
          style = MaterialTheme.typography.h4,
          modifier = Modifier.padding(horizontal = 16.dp),
      )
    }
    items(players) { friend ->
      PersonCard(
          person = friend,
          trailingAction = {
            OutlinedButton(
                onClick = { /*TODO*/},
                shape = RoundedCornerShape(24.dp),
            ) {
              Text(
                  modifier = Modifier.padding(horizontal = 8.dp),
                  text = strings.socialPerformPlay,
              )
            }
          },
      )
    }
  }
}

/**
 * This composable display the screen when the user is [Searching] mode but the input is empty
 *
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun EmptySearch(modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  val color = MaterialTheme.colors.primaryVariant.copy(0.4f)

  Column(
      modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(PawniesIcons.Search, "", modifier = Modifier.size(72.dp), tint = color)
    Text(text = strings.socialSearchEmptyTitle, style = MaterialTheme.typography.h4, color = color)
    Text(
        text = strings.socialSearchEmptySubtitle,
        style = MaterialTheme.typography.body1,
        color = color,
        textAlign = TextAlign.Center)
  }
}

/**
 * This composable display all the players that are in the [SocialScreenState]. This composable also
 * allow user to follow another player
 *
 * @param players A list of [Person] that will be displayed
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun SearchResultList(players: List<Person>, modifier: Modifier = Modifier) {
  LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(players) { player ->
      PersonCard(
          person = player,
          trailingAction = {
            OutlinedButton(
                onClick = { /*TODO*/},
                shape = RoundedCornerShape(24.dp),
            ) {
              Icon(
                  PawniesIcons.Add, contentDescription = LocalLocalizedStrings.current.socialFollow)

              Text(
                  modifier = Modifier.padding(start = 8.dp),
                  text = LocalLocalizedStrings.current.socialFollow,
              )
            }
          })
    }
  }
}
