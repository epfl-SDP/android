@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state

import android.Manifest.permission.RECORD_AUDIO
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.state.game.ActualPuzzleGameScreenState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

/**
 * The [StatefulPuzzleGameScreen] to be used for the Navigation.
 *
 * @param user the currently logged-in user.
 * @param puzzleId the identifier for the puzzle.
 * @param actions the [StatefulGameScreenActions] to perform.
 * @param modifier the [Modifier] for the composable.
 * @param paddingValues the [PaddingValues] for this composable.
 * @param audioPermissionState the [PermissionState] which provides access to audio content.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StatefulPuzzleGameScreen(
    user: AuthenticatedUser,
    puzzleId: String,
    actions: StatefulGameScreenActions,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    audioPermissionState: PermissionState = rememberPermissionState(RECORD_AUDIO),
) {
  val chessFacade = LocalChessFacade.current
  val speechFacade = LocalSpeechFacade.current

  val scope = rememberCoroutineScope()
  val puzzle = chessFacade.puzzle(uid = puzzleId) ?: Puzzle()
  val currentUser = rememberUpdatedState(user)
  val currentActions = rememberUpdatedState(actions)
  val currentStrings = rememberUpdatedState(LocalLocalizedStrings.current)

  val snackbarHostState = remember { SnackbarHostState() }

  val puzzleGameScreenState =
      remember(
          currentActions,
          currentStrings,
          currentUser,
          puzzle,
          audioPermissionState,
          speechFacade,
          scope,
      ) {
        ActualPuzzleGameScreenState(
            currentActions = currentActions,
            currentStrings = currentStrings,
            currentUser = currentUser,
            puzzle = puzzle,
            permission = audioPermissionState,
            speechFacade = speechFacade,
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
      }

  StatefulPromoteDialog(puzzleGameScreenState)

  PuzzleGameScreen(
      state = puzzleGameScreenState,
      modifier = modifier,
      contentPadding = paddingValues,
      snackbarHostState = snackbarHostState,
  )
}
