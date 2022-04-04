package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.Search
import ch.epfl.sdp.mobile.ui.plus
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching

/**
 * This screen displays all registered users of the app.
 *
 * @param P the type of the [Person].
 * @param state the [SocialScreenState], manage the composable contents.
 * @param modifier the [Modifier] for the composable.
 * @param key a function which uniquely identifies the list items.
 * @param contentPadding the [PaddingValues] for this screen.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <P : Person> SocialScreen(
    state: SocialScreenState<P>,
    modifier: Modifier = Modifier,
    key: ((P) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val transition = updateTransition(state.mode, "Social state")

  val followingLazyListState = rememberLazyListState()
  val searchingLazyListState = rememberLazyListState()
  val elevated =
      when (state.mode) {
        Following ->
            followingLazyListState.isScrollInProgress ||
                followingLazyListState.firstVisibleItemIndex > 0 ||
                followingLazyListState.firstVisibleItemScrollOffset > 0
        Searching ->
            searchingLazyListState.isScrollInProgress ||
                searchingLazyListState.firstVisibleItemIndex > 0 ||
                followingLazyListState.firstVisibleItemScrollOffset > 0
      }

  Scaffold(
      modifier = modifier,
      topBar = {
        val elevation by animateDpAsState(if (elevated) 4.dp else 0.dp)
        Surface(elevation = elevation) {
          SearchField(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              value = state.input,
              onValueChange = { state.input = it },
              interactionSource = state.searchFieldInteraction,
          )
        }
      },
      content = { paddingValues ->
        val totalPadding = contentPadding + paddingValues
        transition.AnimatedContent { target ->
          when (target) {
            Following ->
                FollowList(
                    players = state.following,
                    onShowProfileClick = state::onShowProfileClick,
                    lazyListState = followingLazyListState,
                    key = key,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = totalPadding,
                )
            Searching ->
                if (state.input.isEmpty())
                    EmptySearch(
                        modifier = Modifier.padding(16.dp),
                        contentPadding = totalPadding,
                    )
                else
                    SearchResultList(
                        players = state.searchResult,
                        onFollowClick = state::onFollowClick,
                        onShowProfileClick = state::onShowProfileClick,
                        lazyListState = searchingLazyListState,
                        key = key,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = totalPadding,
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
 * @param onShowProfileClick Callback function for click on Item.
 * @param modifier modifier the [Modifier] for the composable.
 * @param lazyListState the [LazyListState] for the list of items.
 * @param key a function which uniquely identifies the list items.
 * @param contentPadding the [PaddingValues] for this list.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <P : Person> FollowList(
    players: List<P>,
    onShowProfileClick: (P) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    key: ((P) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val strings = LocalLocalizedStrings.current
  LazyColumn(
      modifier = modifier.testTag("friendList"),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      state = lazyListState,
      contentPadding = contentPadding,
  ) {
    item {
      Text(
          text = strings.socialFollowingTitle,
          style = MaterialTheme.typography.h4,
          modifier = Modifier.padding(horizontal = 16.dp),
      )
    }
    items(
        items = players,
        key = key,
    ) { friend ->
      PersonItem(
          modifier = Modifier.clickable { onShowProfileClick(friend) }.animateItemPlacement(),
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
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun EmptySearch(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val strings = LocalLocalizedStrings.current
  val color = MaterialTheme.colors.primaryVariant.copy(0.4f)
  Column(
      modifier = modifier.padding(contentPadding),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(PawniesIcons.Search, null, modifier = Modifier.size(72.dp), tint = color)
    Text(text = strings.socialSearchEmptyTitle, style = MaterialTheme.typography.h4, color = color)
    Text(
        text = strings.socialSearchEmptySubtitle,
        style = MaterialTheme.typography.body1,
        color = color,
        textAlign = TextAlign.Center,
    )
  }
}

/**
 * This composable display all the players that are in the [SocialScreenState]. This composable also
 * allow user to follow another player.
 *
 * @param P the type of the [Person].
 * @param players A list of [P] that will be displayed.
 * @param onFollowClick A function to be executed once a [Person]'s follow button is clicked.
 * @param onShowProfileClick A function that is executed if clicked on a result.
 * @param modifier the [Modifier] for the composable.
 * @param lazyListState the [LazyListState] for this list.
 * @param key a function which uniquely identifies the list items.
 * @param contentPadding the [PaddingValues] for this screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <P : Person> SearchResultList(
    players: List<P>,
    onFollowClick: (P) -> Unit,
    onShowProfileClick: (P) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    key: ((P) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(),
) {
  LazyColumn(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      state = lazyListState,
      contentPadding = contentPadding,
  ) {
    items(
        items = players,
        key = key,
    ) { player ->
      PersonItem(
          person = player,
          modifier = modifier.clickable { onShowProfileClick(player) }.animateItemPlacement(),
          trailingAction = {
            FollowButton(
                following = player.followed,
                onClick = { onFollowClick(player) },
            )
          },
      )
    }
  }
}
