package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    private val user: AuthenticatedUser,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
    onEditProfileNameClickAction: State<() -> Unit>,
) : SettingScreenState, Person by ProfileAdapter(user) {
  override val email = user.email
  override var pastGamesCount by mutableStateOf(0)
    private set
  override val puzzlesCount = 0
  override var matches by mutableStateOf(emptyList<ChessMatch>())
    private set

  init {
    scope.launch {
      fetchForUser(user, chessFacade).collect { list ->
        matches = list.map { createChessMatch(it, user) }
        pastGamesCount = matches.size
      }
    }
  }
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
      remember(user) {
        AuthenticatedUserProfileScreenState(user, chessFacade, scope, currentOnEditProfileNameClick)
      }
  SettingsScreen(state, modifier, contentPadding)
}
