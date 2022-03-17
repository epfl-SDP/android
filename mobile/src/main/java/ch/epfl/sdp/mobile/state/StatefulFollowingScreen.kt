package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.flow.*

private data class ProfileAdapter(
    private val profile: Profile,
) : Person {
  override val backgroundColor: Color
    get() = profile.backgroundColor
  override val name: String
    get() = profile.name
  override val emoji: String
    get() = profile.emoji
}

@Composable
fun StatefulFollowingScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val following =
      remember(user) { user.following }.collectAsState(emptyList()).value.map { ProfileAdapter(it) }

  val socialFacade = LocalSocialFacade.current
  val input = remember { mutableStateOf("") }
  val searchResults =
      remember { snapshotFlow { input.value }.flatMapLatest { s -> socialFacade.search(s) } }
          .collectAsState(emptyList())
          .value
          .map { ProfileAdapter(it) }

  val searchFieldInteraction = remember { MutableInteractionSource() }
  val focused by searchFieldInteraction.collectIsFocusedAsState()
  val mode = if (focused) Searching else Following

  SocialScreen(
      SnapshotSocialScreenState(following, input, searchResults, mode, searchFieldInteraction),
      modifier)
}

private class SnapshotSocialScreenState(
    following: List<Person>,
    input: MutableState<String>,
    searchResult: List<Person>,
    mode: SocialScreenState.Mode,
    searchFieldInteraction: MutableInteractionSource
) : SocialScreenState {
  override var following = following
  override var input by input
  override var searchResult = searchResult
  override var mode = mode
  override var searchFieldInteraction = searchFieldInteraction

  // TODO replace with functionality
  override fun onValueChange() {}
}
