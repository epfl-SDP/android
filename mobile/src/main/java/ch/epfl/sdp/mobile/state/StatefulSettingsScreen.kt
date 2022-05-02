package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope

/**
 * An implementation of the [SettingScreenState] that performs [ChessMatch] requests on the current
 * user's profile.
 *
 * @param user the current logged-in [AuthenticatedUser].
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 * @param onEditProfileNameClickAction Callable lambda to navigate to the profile Edit popup
 */
class AuthenticatedUserProfileScreenState(
    user: AuthenticatedUser,
    chessFacade: ChessFacade,
    scope: CoroutineScope,
    onEditProfileNameClickAction: State<() -> Unit>,
) : SettingScreenState, ProfileScreenState by StatefulProfileScreen(user, chessFacade, scope) {
  override val email = user.email
  override val puzzlesCount = 0
  private val onEditProfileNameClickAction by onEditProfileNameClickAction

  override fun onSettingsClick() {}
  override fun onEditProfileNameClick() {
    onEditProfileNameClickAction()
  }
}

/**
 * A stateful composable to visit setting page of the logged-in user
 *
 * @param user the current logged-in user.
 * @param onEditProfileNameClick Callable lambda to navigate to the profile Edit popup
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    onEditProfileNameClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val currentOnEditProfileNameClick = rememberUpdatedState(onEditProfileNameClick)
  val state =
      remember(user, chessFacade, scope, currentOnEditProfileNameClick) {
        AuthenticatedUserProfileScreenState(user, chessFacade, scope, currentOnEditProfileNameClick)
      }
  SettingsScreen(state, modifier, contentPadding)
}
