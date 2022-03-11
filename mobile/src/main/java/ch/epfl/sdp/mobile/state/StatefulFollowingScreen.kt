package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.ui.social.FollowingState
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen

private class SnapshotFollowingState(
    private val following: State<List<AuthenticationFacade.Profile>>,
) : FollowingState {

  private data class ProfileAdapter(
      private val profile: AuthenticationFacade.Profile,
  ) : Person {
    override val backgroundColor: ProfileColor
      get() = profile.backgroundColor
    override val name: String
      get() = profile.name
    override val emoji: String
      get() = profile.emoji
  }

  override val players: List<Person>
    get() = following.value.map { ProfileAdapter(it) }
}

@Composable
fun StatefulFollowingScreen(
    user: AuthenticationFacade.User.Authenticated,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { user.following }.collectAsState(emptyList())

  SocialScreen(SnapshotFollowingState(state), modifier.fillMaxSize())
}
