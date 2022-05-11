package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.*

/**
 * A state which indicates the content of a [PuzzleGameScreen] composable. It will keep track of the
 * values of moves history.
 */
@Stable
interface PuzzleGameScreenState<Piece : ChessBoardState.Piece> :
    MovableChessBoardState<Piece>, MovesInfoState, PuzzleInfoState, SpeechRecognizerState {

  /** A callback which will be invoked when the user clicks on the AR button. */
  fun onArClick()

  /** A callback which will be invoked if the user wants to go back in the hierarchy. */
  fun onBackClick()
}
