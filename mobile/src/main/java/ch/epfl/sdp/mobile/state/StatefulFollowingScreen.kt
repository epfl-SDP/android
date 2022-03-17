package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private data class ProfileAdapter(
    private val profile: Profile,
) : Person {
  override val backgroundColor: Color
    get() = profile.backgroundColor
  override val name: String
    get() = profile.name
  override val emoji: String
    get() = profile.emoji
  override val uid: String
    get() = profile.uid
}

@Composable
fun StatefulFollowingScreen(
    user: AuthenticatedUser,
    dScope: DocumentEditScope,
    modifier: Modifier = Modifier,
) {
  val following =
      remember(user) { user.following }.collectAsState(emptyList()).value.map { ProfileAdapter(it) }

  val socialFacade = LocalSocialFacade.current
  val input = remember { mutableStateOf("") } // MutalStateOf or Snapshot flow
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
      SnapshotSocialScreenState(following, input, searchResults, mode, searchFieldInteraction, socialFacade, dScope, scope),
      modifier.fillMaxSize())
}

private class SnapshotSocialScreenState(
    following: List<Person>,
    input: MutableState<String>,
    searchResult: List<Person>,
    mode: SocialScreenState.Mode,
    searchFieldInteraction: MutableInteractionSource,
    private val facade: SocialFacade,
    private val dScope: DocumentEditScope,
    private val scope: CoroutineScope,
) : SocialScreenState {

  override var following = following
  override var input by input
  override var searchResult = searchResult
  override var mode = mode
  override var searchFieldInteraction = searchFieldInteraction

  override fun onValueChange() {}

  override fun onFollow(followed: Person) {
      scope.launch {
          facade.follow(followed, dScope)
      }
  }
}
