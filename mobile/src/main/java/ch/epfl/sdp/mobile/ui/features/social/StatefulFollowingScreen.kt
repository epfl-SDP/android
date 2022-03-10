package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.ProfileColor

private class SnapshotFollowingState(
    private val following: State<List<AuthenticationApi.Profile>>,
) : FollowingState {

  private data class ProfileAdapter(
      private val profile: AuthenticationApi.Profile,
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
    user: AuthenticationApi.User.Authenticated,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { user.following }.collectAsState(emptyList())

  SocialScreen(SnapshotFollowingState(state), modifier.fillMaxSize())
}
