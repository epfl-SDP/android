package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthenticatedUserProfileScreenState(
    actions: State<ProfileActions>,
    private val user: AuthenticatedUser,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : SettingScreenState<ChessMatchAdapter> {
  private val actions by actions

  override val email = user.email
  override var pastGamesCount by mutableStateOf(0)
    private set
  override val puzzlesCount = 0
  override var matches by mutableStateOf(emptyList<ChessMatchAdapter>())
    private set

  init {
    scope.launch {
      fetchForUser(user, chessFacade).collect { list ->
        matches = list.map { createChessMatch(it, user) }
        pastGamesCount = matches.size
      }
    }
  }
  override val backgroundColor = Color.Orange
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed

  override fun onSettingsClick() {}
  override fun onEditClick() {}
  override fun onMatchClick(match: ChessMatchAdapter) = actions.onMatchClick(match)
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param onMatchClick callback function called when a match is clicked on.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    onMatchClick: (ChessMatchAdapter) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val actions = rememberUpdatedState(ProfileActions(onMatchClick = onMatchClick))
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val state =
      remember(user) { AuthenticatedUserProfileScreenState(actions, user, chessFacade, scope) }
  SettingsScreen(state, modifier, contentPadding)
}
