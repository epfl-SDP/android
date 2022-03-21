package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private data class ProfileAdapter(
    val profile: Profile,
) : Person {
  override val backgroundColor = profile.backgroundColor
  override val name = profile.name
  override val emoji = profile.emoji
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
  val focused = searchFieldInteraction.collectIsFocusedAsState()
  val mode = if (focused.value) Searching else Following
  val scope = rememberCoroutineScope()

  SocialScreen(
      SnapshotSocialScreenState(
          user = user,
          following = following,
          input = input,
          searchResult = searchResults,
          mode = mode,
          searchFieldInteraction = searchFieldInteraction,
          scope = scope,
      ),
      modifier.fillMaxSize())
}

private class SnapshotSocialScreenState(
    private val user: AuthenticatedUser,
    following: List<ProfileAdapter>,
    input: MutableState<String>,
    searchResult: List<ProfileAdapter>,
    mode: SocialScreenState.Mode,
    searchFieldInteraction: MutableInteractionSource,
    private val scope: CoroutineScope,
) : SocialScreenState<ProfileAdapter> {

  override var following = following
  override var input by input
  override var searchResult = searchResult
  override var mode = mode
  override var searchFieldInteraction = searchFieldInteraction

  override fun onValueChange() {}

  override fun onFollowClick(followed: ProfileAdapter) {
    scope.launch { user.follow(followed.profile) }
  }
}
