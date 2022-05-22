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
import kotlinx.coroutines.launch

/**
 * An implementation of the [SettingScreenState] that performs [ChessMatch] requests on the current
 * user's profile.
 *
 * @param actions the [ProfileActions] which are available on the screen.
 * @param user the current logged-in [AuthenticatedUser].
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 * @param onEditProfileNameClickAction Callable lambda to navigate to the profile edit popup.
 * @param onEditProfileImageClickAction Callable lambda to navigate to the profile image edit popup.
 */
class AuthenticatedUserProfileScreenState(
    actions: State<ProfileActions>,
    private val user: AuthenticatedUser,
    chessFacade: ChessFacade,
    private val scope: CoroutineScope,
    onEditProfileNameClickAction: State<() -> Unit>,
    onEditProfileImageClickAction: State<() -> Unit>,
) :
    SettingScreenState<ChessMatchAdapter>,
    ProfileScreenState<ChessMatchAdapter> by StatefulProfileScreen(
        user, actions, chessFacade, scope) {
  override val email = user.email
  override val puzzlesCount = 0
  private val onEditProfileNameClickAction by onEditProfileNameClickAction
  private val onEditProfileImageClickAction by onEditProfileImageClickAction

  override fun onEditProfileNameClick() {
    onEditProfileNameClickAction()
  }
  override fun onEditProfileImageClick() {
    onEditProfileImageClickAction()
  }
  override fun onLogout() {
    scope.launch { user.signOut() }
  }
}

/**
 * A stateful composable to visit setting page of the logged-in user.
 *
 * @param user the current logged-in user.
 * @param onMatchClick callback function called when a match is clicked on.
 * @param onEditProfileNameClick Callable lambda to navigate to the profile edit popup.
 * @param onEditProfileImageClick Callable lambda to navigate to the profile image edit popup.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    onMatchClick: (ChessMatchAdapter) -> Unit,
    onEditProfileNameClick: () -> Unit,
    onEditProfileImageClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val actions = rememberUpdatedState(ProfileActions(onMatchClick = onMatchClick, onBack = {}))
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val currentOnEditProfileNameClick = rememberUpdatedState(onEditProfileNameClick)
  val currentOnEditProfileImageClick = rememberUpdatedState(onEditProfileImageClick)

  val state =
      remember(
          actions,
          user,
          chessFacade,
          scope,
          currentOnEditProfileNameClick,
          currentOnEditProfileImageClick) {
        AuthenticatedUserProfileScreenState(
            actions,
            user,
            chessFacade,
            scope,
            currentOnEditProfileNameClick,
            currentOnEditProfileImageClick,
        )
      }
  SettingsScreen(state, modifier, contentPadding)
}
