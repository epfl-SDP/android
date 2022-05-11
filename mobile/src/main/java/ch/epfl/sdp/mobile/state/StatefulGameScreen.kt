@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state

import android.Manifest.permission.RECORD_AUDIO
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.game.ActualGameScreenState
import ch.epfl.sdp.mobile.ui.game.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

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
 * @param audioPermissionState the [PermissionState] which provides access to audio content.
 */
@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    id: String,
    actions: StatefulGameScreenActions,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    audioPermissionState: PermissionState = rememberPermissionState(RECORD_AUDIO),
) {
  val chessFacade = LocalChessFacade.current
  val speechFacade = LocalSpeechFacade.current

  val scope = rememberCoroutineScope()
  val match = remember(chessFacade, id) { chessFacade.match(id) }

  val snackbarHostState = remember { SnackbarHostState() }
  val gameScreenState =
      remember(actions, user, match, audioPermissionState, speechFacade, snackbarHostState, scope) {
        ActualGameScreenState(
            actions = actions,
            user = user,
            match = match,
            permission = audioPermissionState,
            speechFacade = speechFacade,
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
      }

  StatefulPromoteDialog(gameScreenState)

  GameScreen(
      state = gameScreenState,
      modifier = modifier,
      contentPadding = paddingValues,
      snackbarHostState = snackbarHostState,
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
