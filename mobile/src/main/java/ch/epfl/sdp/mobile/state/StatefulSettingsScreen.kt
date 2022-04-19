package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch

class SettingsScreenStateImpl(
    user: AuthenticatedUser,
    onEditProfileNameClickAction: State<() -> Unit>
) : SettingScreenState {
  override val email = user.email
  override val pastGamesCount = 0
  override val puzzlesCount = 0
  override val matches = emptyList<ChessMatch>()
  override val backgroundColor = Color.Orange
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed
  private val onEditProfileNameClickAction by onEditProfileNameClickAction

  override fun onSettingsClick() {}
  override fun onEditProfileNameClick() {
    onEditProfileNameClickAction()
  }
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param onEditProfileNameClick Callable lambda to navigate to the profile Edit popup
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    onEditProfileNameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val currentOnEditProfileNameClick = rememberUpdatedState(onEditProfileNameClick)

  val state = remember(user) { SettingsScreenStateImpl(user, currentOnEditProfileNameClick) }
  SettingsScreen(state, modifier)
}
