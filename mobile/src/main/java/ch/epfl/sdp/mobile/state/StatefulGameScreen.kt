package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.state.game.MatchGameScreenState
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Message.*

/**
 * The different navigation actions which may be performed by the [StatefulGameScreen].
 *
 * @param onBack the action to perform when going back.
 * @param onShowAr the action to perform when AR should be started for the match.
 */
data class StatefulGameScreenActions(
    val onBack: () -> Unit,
    val onShowAr: (Match) -> Unit,
)

/**
 * The [StatefulGameScreen] to be used for the Navigation
 *
 * @param user the currently logged-in user.
 * @param id the identifier for the match.
 * @param actions the [StatefulGameScreenActions] to perform.
 * @param modifier the [Modifier] for the composable.
 * @param paddingValues the [PaddingValues] for this composable.
 */
@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    id: String,
    actions: StatefulGameScreenActions,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) {
  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val match = remember(facade, id) { facade.match(id) }

  val gameScreenState =
      remember(actions, user, match, scope) {
        MatchGameScreenState(
            actions = actions,
            user = user,
            match = match,
            scope = scope,
        )
      }

  StatefulPromoteDialog(gameScreenState)

  GameScreen(
      state = gameScreenState,
      modifier = modifier,
      contentPadding = paddingValues,
  )
}

/**
 * A composable which displays a full-screen dialog for the [PromotionState].
 *
 * @param state the [PromotionState] which backs this dialog.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun StatefulPromoteDialog(
    state: PromotionState,
    modifier: Modifier = Modifier,
) {
  if (state.choices.isNotEmpty()) {
    PromoteDialog(
        // Always use the Black color to remain consistent with Figma ?
        color = ChessBoardState.Color.Black,
        selected = state.selection,
        onSelectRank = state::onSelect,
        onConfirm = state::onConfirm,
        confirmEnabled = state.confirmEnabled,
        choices = state.choices,
        modifier = modifier,
    )
  }
}
