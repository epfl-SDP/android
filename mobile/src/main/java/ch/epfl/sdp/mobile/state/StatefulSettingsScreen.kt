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

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticatedUser,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
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

  override fun onSettingsClick() {}
  override fun onEditClick() {}
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val state = remember(user) { AuthenticatedUserProfileScreenState(user, chessFacade, scope) }
  SettingsScreen(state, modifier, contentPadding)
}
