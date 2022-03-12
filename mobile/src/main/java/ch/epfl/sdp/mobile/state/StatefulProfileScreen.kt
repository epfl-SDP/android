package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileState
import ch.epfl.sdp.mobile.ui.social.ChessMatch

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticatedUser,
) : ProfileState {
  override val email = user.email
  override val pastGamesCount = 0
  override val puzzlesCount = 0
  override val matches = emptyList<ChessMatch>()
  override val backgroundColor = ProfileColor.Orange
  override val name = user.name
  override val emoji = user.emoji

  override fun onSettingsClick() {}
  override fun onEditClick() {}
}

@Composable
fun StatefulProfileScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { AuthenticatedUserProfileScreenState(user) }
  ProfileScreen(state, modifier)
}
