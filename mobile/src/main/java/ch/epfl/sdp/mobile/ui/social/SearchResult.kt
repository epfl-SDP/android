package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * This composable display all the players that are in the [SearchState]. This composable also allow
 * user to follow another player
 *
 * @param state the [SearchState], manage the composable contents
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun SearchResultList(state: SearchState, modifier: Modifier = Modifier) {
  LazyColumn(modifier) {
    items(state.players) { player -> FriendCard(person = player, mode = SocialMode.Follow) }
  }
}
