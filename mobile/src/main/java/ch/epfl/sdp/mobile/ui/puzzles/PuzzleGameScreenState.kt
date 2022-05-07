package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.game.GameScreenState.*

/**
 * A state which indicates the content of a [PuzzleGameScreen] composable. It will keep track of the
 * values of moves history.
 */
@Stable
interface PuzzleGameScreenState<Piece : ChessBoardState.Piece> :
    MovableChessBoardState<Piece>, SpeechRecognizerState {
  /** The [Puzzle] that is loaded */
  val puzzleInfo: PuzzleInfo

  /** Marks the [Puzzle] as solved for the current user */
  fun solve()

  /**
   * A [List] of all the moves which have been performed by the user. Moves are ordered and should
   * be displayed as such.
   */
  val moves: List<Move>

  /** A callback which will be invoked if the user wants to go back in the hierarchy. */
  fun onBackClick()

  var puzzleState: PuzzleState

  enum class PuzzleState {
    Solving,
    Failed,
    Solved,
  }
}
