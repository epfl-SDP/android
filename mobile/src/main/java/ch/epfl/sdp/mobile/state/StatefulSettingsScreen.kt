package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * Implementation of the [SettingScreenState], [Person], [ProfileAdapter] Interfacces
 *
 * @param user the current logged-in user.
 * @param onEditProfileNameClick Callable lambda to navigate to the profile Edit popup
 */
class SettingsScreenStateImpl(
    user: AuthenticatedUser,
    onEditProfileNameClickAction: State<() -> Unit>
) : SettingScreenState, Person by ProfileAdapter(user) {
  override val email = user.email
  override val pastGamesCount = 0
  override val puzzlesCount = 0
  override val matches = emptyList<ChessMatch>()
  private val onEditProfileNameClickAction by onEditProfileNameClickAction
  private val onEditProfileImageClickAction by onEditProfileImageClickAction

  override fun onEditProfileImageClick() {
    onEditProfileImageClickAction()
  }
  override fun onEditProfileNameClick() {
    onEditProfileNameClickAction()
  }
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param onEditProfileNameClick Callable lambda to navigate to the profile name edit popup
 * @param onEditProfileImageClickAction Callable lambda to navigate to the profile image edit popup
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    onEditProfileNameClick: () -> Unit,
    onEditProfileImageClickAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val currentOnEditProfileNameClick = rememberUpdatedState(onEditProfileNameClick)
  val currentOnEditProfileImageClickAction = rememberUpdatedState(onEditProfileImageClickAction)

  val state =
      remember(user) {
        SettingsScreenStateImpl(
            user, currentOnEditProfileNameClick, currentOnEditProfileImageClickAction)
      }
  SettingsScreen(state, modifier)
}
