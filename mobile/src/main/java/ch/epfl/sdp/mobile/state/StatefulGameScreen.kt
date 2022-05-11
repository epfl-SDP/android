@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state

import android.Manifest.permission.RECORD_AUDIO
import androidx.compose.foundation.MutatePriority.UserInput
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.Failure
import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.Success
import ch.epfl.sdp.mobile.state.game.MatchGameScreenState
import ch.epfl.sdp.mobile.ui.game.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
  val match = remember(chessFacade, id) { chessFacade.match(id, user) }

  val snackbarHostState = remember { SnackbarHostState() }
  val speechRecognizerState =
      remember(audioPermissionState, speechFacade, snackbarHostState, scope) {
        SnackbarSpeechRecognizerState(
            permission = audioPermissionState,
            facade = speechFacade,
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
      }
  val gameScreenState =
      remember(actions, user, match, scope) {
        MatchGameScreenState(
            actions = actions,
            user = user,
            match = match,
            scope = scope,
            speechRecognizerState = speechRecognizerState,
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

/**
 * An implementation of [SpeechRecognizerState] which will display the results in a
 * [SnackbarHostState].
 *
 * @param permission the [PermissionState] for the microphone permission.
 * @param facade the [SpeechFacade] which is used.
 * @param snackbarHostState the [SnackbarHostState] used to display some results.
 * @param scope the [CoroutineScope] in which the actions are performed.
 */
class SnackbarSpeechRecognizerState
constructor(
    private val permission: PermissionState,
    private val facade: SpeechFacade,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
) : SpeechRecognizerState {

  override var listening: Boolean by mutableStateOf(false)
    private set

  /**
   * A [MutatorMutex] which ensures that multiple speech recognition requests aren't performed
   * simultaneously, and that clicking on the button again cancels the previous request.
   */
  private val mutex = MutatorMutex()

  override fun onListenClick() {
    scope.launch {
      val willCancel = listening
      mutex.mutate(UserInput) {
        try {
          if (willCancel) return@mutate
          listening = true
          if (!permission.hasPermission) {
            permission.launchPermissionRequest()
          } else {
            when (val speech = facade.recognize()) {
              // TODO : Display an appropriate message, otherwise act on the board.
              Failure.Internal -> snackbarHostState.showSnackbar("Internal failure")
              is Success -> for (result in speech.results) snackbarHostState.showSnackbar(result)
            }
          }
        } finally {
          listening = false
        }
      }
    }
  }
}
