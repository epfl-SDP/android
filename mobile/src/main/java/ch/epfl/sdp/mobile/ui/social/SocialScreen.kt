package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
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
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.Search
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching

/**
 * This screen displays all registered users of the app.
 *
 * @param P the type of the [Person].
 * @param state the [SocialScreenState], manage the composable contents.
 * @param modifier the [Modifier] for the composable.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <P : Person> SocialScreen(
    state: SocialScreenState<P>,
    modifier: Modifier = Modifier,
) {
  val transition = updateTransition(state.mode, "Social state")

  Scaffold(
      modifier = modifier,
      topBar = {
        Surface(elevation = 0.dp) {
          SearchField(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              value = state.input,
              onValueChange = { state.input = it },
              interactionSource = state.searchFieldInteraction,
          )
        }
      },
      content = {
        transition.AnimatedContent { target ->
          when (target) {
            Following ->
                FollowList(
                    players = state.following,
                    onPersonClick = state::onPersonClick,
                )
            Searching ->
                if (state.input.isEmpty()) EmptySearch()
                else
                    SearchResultList(
                        players = state.searchResult,
                        onClick = state::onFollowClick,
                        onPersonClick = state::onPersonClick,
                    )
          }
        }
      },
  )
}

/**
 * Display the list of followed player.
 *
 * @param P the type of the [Person].
 * @param players A list of [Person] that need to be displayed.
 * @param onPersonClick Callback function for click on Item.
 * @param modifier modifier the [Modifier] for the composable.
 */
@Composable
fun <P : Person> FollowList(
    players: List<P>,
    onPersonClick: (P) -> Unit,
    modifier: Modifier = Modifier,
) {
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
          modifier = Modifier.clickable { onPersonClick(friend) },
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
 * This composable display the screen when the user is [Searching] mode but the input is empty.
 *
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun EmptySearch(
    modifier: Modifier = Modifier,
) {
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
 * allow user to follow another player.
 *
 * @param P the type of the [Person].
 * @param players A list of [P] that will be displayed.
 * @param onClick A function to be executed once a [Person]'s follow button is clicked.
 * @param onPersonClick A function that is executed if clicked on a result.
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun <P : Person> SearchResultList(
    players: List<P>,
    onClick: (P) -> Unit,
    onPersonClick: (P) -> Unit,
    modifier: Modifier = Modifier,
) {
  LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(players) { player ->
      PersonCard(
          person = player,
          modifier = modifier.clickable { onPersonClick(player) },
          trailingAction = {
            FollowButton(
                following = player.followed,
                onClick = { onClick(player) },
            )
          },
      )
    }
  }
}
