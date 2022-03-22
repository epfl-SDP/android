package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.flow.*

data class ProfileAdapter(
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
    controller: NavHostController,
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

  val openProfile: (person: Person) -> Unit = { person ->
    controller.navigate("$ProfileRoute/${person.name}")
  }

  SocialScreen(
      SnapshotSocialScreenState(
          following, input, searchResults, mode, openProfile, searchFieldInteraction),
      modifier)
}

private class SnapshotSocialScreenState(
    override var following: List<Person>,
    input: MutableState<String>,
    override var searchResult: List<Person>,
    override var mode: SocialScreenState.Mode,
    override val openProfile: (Person) -> Unit,
    override var searchFieldInteraction: MutableInteractionSource
) : SocialScreenState {
  override var input by input

  // TODO replace with functionality
  override fun onValueChange() {}
}
