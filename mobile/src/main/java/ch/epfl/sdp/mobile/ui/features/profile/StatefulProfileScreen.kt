package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.features.authentication.AuthenticatedUserProfileScreenState

@Composable
fun StatefulProfileScreen(
    user: AuthenticationApi.User.Authenticated,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { AuthenticatedUserProfileScreenState(user) }
  ProfileScreen(state, modifier)
}
