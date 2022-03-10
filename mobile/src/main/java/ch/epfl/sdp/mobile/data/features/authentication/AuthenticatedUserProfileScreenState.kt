package ch.epfl.sdp.mobile.data.features.authentication

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.ProfileColor
import ch.epfl.sdp.mobile.ui.features.profile.ProfileState
import ch.epfl.sdp.mobile.ui.features.social.ChessMatch

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticationApi.User.Authenticated,
) : ProfileState {
  override val email = user.email
  override val pastGamesCount = 0
  override val puzzlesCount = 0
  override val matches = emptyList<ChessMatch>()
  override val backgroundColor = ProfileColor.Pink
  override val name = user.name
  override val emoji = user.emoji

  override fun onSettingsClick() {}
  override fun onEditClick() {}
}
