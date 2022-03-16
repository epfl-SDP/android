package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.fillMaxSize
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

private class SnapshotSocialScreenState(
    private val following: State<List<Profile>>,
) : SocialScreenState {

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

  override var mode: SocialScreenState.Mode by mutableStateOf(Following)

  override var players: List<Person> = following.value.map { ProfileAdapter(it) }
  override var input: String by mutableStateOf("")

  // TODO :  define how to return to the following screen, return button ?
  // TODO :  Modify here to update the player list when we do a search
  override fun onValueChange() {
    if (mode != Following) {
      mode = Searching
    }
  }
}

@Composable
fun StatefulFollowingScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { user.following }.collectAsState(emptyList())

  SocialScreen(SnapshotSocialScreenState(state), modifier.fillMaxSize())
}
