package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch

class AuthenticatedUserProfileScreenState(
  user: AuthenticatedUser,
  openEditProfileNameAction: State<() -> Unit>
) : SettingScreenState {
  override val email = user.email
  override val pastGamesCount = 0
  override val puzzlesCount = 0
  override val matches = emptyList<ChessMatch>()
  override val backgroundColor = Color.Orange
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed
  val openEditProfileNameAction by openEditProfileNameAction


  override fun onSettingsClick() {}
  override fun openEditProfileName() {
    openEditProfileNameAction()
  }
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param navigateToProfileNameEdit Callable lambda to navigate to the profile Edit popup
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    openEditProfileName: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val currentOpenEditProfileName = rememberUpdatedState(openEditProfileName)

  val state = remember(user) { AuthenticatedUserProfileScreenState(user, currentOpenEditProfileName) }
  SettingsScreen(state, modifier)
}
