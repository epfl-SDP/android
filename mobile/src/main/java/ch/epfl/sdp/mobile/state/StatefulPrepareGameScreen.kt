package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState.ColorChoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A composable that makes a [PrepareGameScreen] stateful.
 * @param user authenticated user.
 * @param opponentId the id of the opponent if it exists.
 * @param navigateToGame The action to take for navigating to a created game.
 * @param cancelClick The action to take when clicking on the dialog's cancel button.
 * @param modifier [Modifier] for this composable.
 */
@Composable
fun StatefulPrepareGameScreen(
    user: AuthenticatedUser,
    opponentId: String?,
    navigateToGame: (match: Match) -> Unit,
    cancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val chessFacade = LocalChessFacade.current
  val opponents =
      remember(user) { user.following }.collectAsState(emptyList()).value.map { ProfileAdapter(it) }
  val state =
      remember(user, opponents, navigateToGame, cancelClick, scope, chessFacade, opponentId) {
        SnapshotPrepareGameScreenState(
            user = user,
            selected = opponentId,
            opponents = opponents,
            navigateToGame = navigateToGame,
            cancelClick = cancelClick,
            chessFacade = chessFacade,
            scope = scope,
        )
      }

  PrepareGameScreen(state = state, modifier = modifier, key = { it.uid })
}

/**
 * An implementation of the [PrepareGameScreenState].
 *
 * @param user The current [AuthenticatedUser].
 * @param selected the item which is selected.
 * @param navigateToGame The action to take for navigating to a created game.
 * @param cancelClick The action to take when clicking on the dialog's cancel button.
 * @param chessFacade The chess facade to act on the store.
 * @param scope The [Composable]'s [CoroutineScope].
 */
class SnapshotPrepareGameScreenState(
    val user: AuthenticatedUser,
    selected: String?,
    override val opponents: List<ProfileAdapter>,
    val navigateToGame: (match: Match) -> Unit,
    val cancelClick: () -> Unit,
    val chessFacade: ChessFacade,
    val scope: CoroutineScope,
) : PrepareGameScreenState<ProfileAdapter> {

  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)

  override var selectedOpponent: ProfileAdapter? by
      mutableStateOf(opponents.firstOrNull { it.uid == selected })
    private set

  override var playEnabled: Boolean by mutableStateOf(selectedOpponent != null)
    private set

  override fun onOpponentClick(opponent: ProfileAdapter) {
    if (selectedOpponent == opponent) {
      selectedOpponent = null
      playEnabled = false
    } else {
      selectedOpponent = opponent
      playEnabled = true
    }
  }

  override fun onPlayClick() {
    selectedOpponent?.let {
      scope.launch {
        val (white, black) =
            when (colorChoice) {
              ColorChoice.White -> ProfileAdapter(user) to it
              ColorChoice.Black -> it to ProfileAdapter(user)
            }
        val match = chessFacade.createMatch(white = white.profile, black = black.profile, user)
        navigateToGame(match)
      }
    }
  }

  override fun onCancelClick() = this.cancelClick()
}
