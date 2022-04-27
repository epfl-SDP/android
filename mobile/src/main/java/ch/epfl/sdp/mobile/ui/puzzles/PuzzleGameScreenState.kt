package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.MovableChessBoardState

/**
 * A state which indicates the content of a [PuzzleGameScreen] composable. It will keep track of the
 * values of moves history.
 *
 * @param Piece the type of the pieces of the underlying [ChessBoardState].
 */
@Stable
interface PuzzleGameScreenState<Piece : ChessBoardState.Piece> {
  val puzzle: Puzzle
}
