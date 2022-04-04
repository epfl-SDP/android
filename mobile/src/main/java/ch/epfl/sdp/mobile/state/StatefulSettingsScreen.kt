package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticatedUser,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : SettingScreenState {
  override val email = user.email
  override var pastGamesCount by mutableStateOf(0)
  override val puzzlesCount = 0
  override var matches by mutableStateOf(emptyList<ChessMatch>())

  init {
    scope.launch {
      chessFacade
          .chessMatches(user)
          .onEach {
            matches = it
            pastGamesCount = matches.size
          }
          .collect()
    }
  }
  override val backgroundColor = Color.Orange
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed

  override fun onSettingsClick() {}
  override fun onEditClick() {}
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val state = remember(user) { AuthenticatedUserProfileScreenState(user, chessFacade, scope) }
  SettingsScreen(state, modifier)
}
