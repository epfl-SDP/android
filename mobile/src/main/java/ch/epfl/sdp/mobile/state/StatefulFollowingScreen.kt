package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A class that turns a provided [Profile] into a [Person].
 *
 * @param profile the [Profile] to turn into a [Person].
 */
private data class ProfileAdapter(
    val profile: Profile,
) : Person {
  override val backgroundColor = profile.backgroundColor
  override val name = profile.name
  override val emoji = profile.emoji
}

/**
 * A stateful implementation of the [SocialScreen] composable, which uses some composition-local
 * values to retrieve the appropriate dependencies.
 *
 * @param user the current [AuthenticatedUser].
 * @param modifier the [Modifier] for this composable.
 */
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
      modifier)
}

/**
 * An implementation of the [SocialScreenState] that performs social requests.
 *
 * It uses a [ProfileAdapter] as the generic [Person] type to be able to retrieve the [Person]'s uid
 * by converting it to a [Profile]
 *
 * @param user the current [AuthenticatedUser].
 * @param following the list of [ProfileAdapter]s that are being followed by the current user.
 * @param input the typed text field input [String].
 * @param searchResult a list of [ProfileAdapter]s that are displayed after a user's profile search.
 * @param mode the current [SocialScreenState.Mode] of the social screen.
 * @param searchFieldInteraction the [MutableInteractionSource] of the search field.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
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
