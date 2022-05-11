@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state.game

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.State
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.core.PuzzleGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.*
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreenState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfoState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a new [ActualPuzzleGameScreenState].
 *
 * @param currentActions the [State] of actions to perform when navigating.
 * @param currentUser the currently authenticated user.
 * @param puzzle the [Puzzle] to display.
 * @param permission the [PermissionState] for the microphone permission.
 * @param speechFacade the [SpeechFacade] to acecess speech recognition facilities.
 * @param snackbarHostState the [SnackbarHostState] to display some info.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 */
fun ActualPuzzleGameScreenState(
    currentActions: State<StatefulGameScreenActions>,
    currentUser: State<AuthenticatedUser>,
    puzzle: Puzzle,
    permission: PermissionState,
    speechFacade: SpeechFacade,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
): ActualPuzzleGameScreenState {
  val puzzleInfo = DelegatingPuzzleInfoState(puzzle)

  // The backing MutableGameDelegate.
  val chessBoard = PuzzleGameDelegate(currentUser, puzzle, puzzleInfo, scope)

  // The delegates which use the MutableGameDelegate.
  val promotions = DelegatingPromotionState(chessBoard)
  val moves = DelegatingMovesInfoState(chessBoard)
  val moveableChessBoard =
      DelegatingPuzzlePromotionMovableChessBoardState(
          currentUser.value,
          chessBoard,
          puzzleInfo,
          promotions,
      )
  val speechRecognizer =
      DelegatingSpeechRecognizerState(
          chessBoard,
          permission,
          speechFacade,
          snackbarHostState,
          scope,
      )
  return ActualPuzzleGameScreenState(
      currentActions = currentActions,
      puzzle = puzzle,
      moveableChessBoard = moveableChessBoard,
      promotionState = promotions,
      movesInfo = moves,
      puzzleInfo = puzzleInfo,
      speechRecognizer = speechRecognizer,
  )
}

/**
 * An implementation of [PuzzleGameScreenState] which delegates its behaviors to different
 * underlying states.
 *
 * @param currentActions the [State] of actions to perform when navigating.
 * @param puzzle the [Puzzle] for this screen.
 * @param moveableChessBoard the underlying [MovableChessBoardState].
 * @param promotionState the underlying [PromotionState].
 * @param movesInfo the underlying [MovesInfoState].
 * @param puzzleInfo the underlying [PuzzleInfoState].
 * @param speechRecognizer the underlying [SpeechRecognizerState].
 */
class ActualPuzzleGameScreenState
constructor(
    private val currentActions: State<StatefulGameScreenActions>,
    private val puzzle: Puzzle,
    moveableChessBoard: MovableChessBoardState<Piece>,
    promotionState: PromotionState,
    movesInfo: MovesInfoState,
    puzzleInfo: PuzzleInfoState,
    speechRecognizer: SpeechRecognizerState,
) :
    PuzzleGameScreenState<Piece>,
    MovableChessBoardState<Piece> by moveableChessBoard,
    PromotionState by promotionState,
    MovesInfoState by movesInfo,
    PuzzleInfoState by puzzleInfo,
    SpeechRecognizerState by speechRecognizer {

  override fun onBackClick() = currentActions.value.onBack()
}
