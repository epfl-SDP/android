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
 * A composable that makes a [PrepareGameScreen] stateful
 * @param user authenticated user
 * @param navigateToGame The action to take for navigating to a created game
 * @param cancelClick The action to take when clicking on the dialog's cancel button
 * @param modifier [Modifier] for this composable
 */
@Composable
fun StatefulPrepareGameScreen(
    user: AuthenticatedUser,
    navigateToGame: (match: Match) -> Unit,
    cancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val chessFacade = LocalChessFacade.current
  val opponents =
      remember(user) { user.following }.collectAsState(emptyList()).value.map { ProfileAdapter(it) }

  val state =
      remember(user, opponents, navigateToGame, cancelClick, scope, chessFacade) {
        SnapshotPrepareGameScreenState(
            user = user,
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
 * An implementation of the [PrepareGameScreenState]
 *
 * @param user The current [AuthenticatedUser]
 * @param navigateToGame The action to take for navigating to a created game
 * @param cancelClick The action to take when clicking on the dialog's cancel button
 * @param chessFacade The chess facade to act on the store
 * @param scope The [Composable]'s [CoroutineScope]
 */
class SnapshotPrepareGameScreenState(
    val user: AuthenticatedUser,
    override val opponents: List<ProfileAdapter>,
    val navigateToGame: (match: Match) -> Unit,
    val cancelClick: () -> Unit,
    val chessFacade: ChessFacade,
    val scope: CoroutineScope,
) : PrepareGameScreenState<ProfileAdapter> {

  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)

  override var selectedOpponent: ProfileAdapter? by mutableStateOf(null)
  private set

  override fun onOpponentClick(opponent: ProfileAdapter) {
    selectedOpponent =
        if (selectedOpponent == opponent) {
          null
        } else {
          opponent
        }
  }

  override fun onPlayClick(opponent: ProfileAdapter) {
    scope.launch {
      val (white, black) =
          when (colorChoice) {
            ColorChoice.White -> ProfileAdapter(user) to opponent
            ColorChoice.Black -> opponent to ProfileAdapter(user)
          }
      val match = chessFacade.createMatch(white = white.profile, black = black.profile)
      navigateToGame(match)
    }
  }

  override fun onCancelClick() = this.cancelClick()
}
