package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * A state which indicates the content of a [GameScreen] composable. It will keep track of the
 * values of moves history.
 *
 * @param Piece the type of the pieces of the underlying [ChessBoardState].
 */
@Stable
interface GameScreenState<Piece : ChessBoardState.Piece> :
    MovableChessBoardState<Piece>,
    MovesInfoState,
    PlayersInfoState,
    SpeechRecognizerState,
    TextToSpeechState {

  /** A callback which will be invoked when the user clicks on the AR button. */
  fun onArClick()

  /** A callback which will be invoked if the user wants to go back in the hierarchy. */
  fun onBackClick()
}
