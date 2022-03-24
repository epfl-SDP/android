package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.Search
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.*

/**
 * This screen displays all registered users of the app.
 *
 * @param P the type of the [Person].
 * @param state the [SocialScreenState], manage the composable contents.
 * @param modifier the [Modifier] for the composable.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <P : Person> SocialScreen(state: SocialScreenState<P>, modifier: Modifier = Modifier) {

  val strings = LocalLocalizedStrings.current

  val transition = updateTransition(state.mode, "Social state")

  Column(
      modifier.padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.input,
        onValueChange = {
          state.input = it
          state.onValueChange()
        },
        placeholder = {
          Text(
              text = strings.socialSearchBarPlaceHolder,
              modifier = Modifier,
              textAlign = TextAlign.Center)
        },
        leadingIcon = { Icon(PawniesIcons.Search, contentDescription = "") },
        singleLine = true,
        shape = RoundedCornerShape(56.dp),
        interactionSource = state.searchFieldInteraction,
        colors =
            TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.onPrimary.copy(0.15f),
                textColor = MaterialTheme.colors.primaryVariant,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent),
    )
    transition.AnimatedContent { target ->
      when (target) {
        Following -> FollowList(state.following, state.openProfile)
        Searching ->
            if (state.input.isEmpty()) EmptySearch()
            else SearchResultList(players = state.searchResult, onClick = state::onFollowClick)
      }
    }
  }
}

/**
 * Display the list of followed player.
 *
 * @param P the type of the [Person].
 * @param players A list of [Person] that need to be displayed.
 * @param modifier modifier the [Modifier] for the composable.
 */
@Composable
fun <P : Person> FollowList(players: List<P>, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  Column(modifier) {
    Text(
        text = strings.socialFollowingTitle,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp))
    LazyColumn(Modifier.testTag("friendList"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
      items(players) { friend ->
        PersonCard(
            person = friend,
            openProfile = openProfile,
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
            })
      }
    }
  }
}

/**
 * This composable display the screen when the user is [Searching] mode but the input is empty.
 *
 * @param modifier the [Modifier] for the composable.
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
 * allow user to follow another player.
 *
 * @param P the type of the [Person].
 * @param players A list of [P] that will be displayed.
 * @param onClick A function to be executed once a [Person]'s follow button is clicked.
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun <P : Person> SearchResultList(
    players: List<P>,
    onClick: (P) -> Unit,
    modifier: Modifier = Modifier,
) {
  LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(players) { player ->
      PersonCard(
          person = player,
          openProfile = openProfile,
          trailingAction = {
            OutlinedButton(
                onClick = { onClick(player) },
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
