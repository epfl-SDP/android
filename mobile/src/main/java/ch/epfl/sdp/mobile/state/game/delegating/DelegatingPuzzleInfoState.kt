package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.state.toPuzzleInfoAdapter
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfoState

/**
 * An implementation of [PuzzleInfoState] that displays a [Puzzle]'s state informations.
 *
 * @param puzzle the [Puzzle] in question
 */
class DelegatingPuzzleInfoState(
    puzzle: Puzzle,
) : PuzzleInfoState {
  override val puzzleInfo = puzzle.toPuzzleInfoAdapter()
  override var puzzleState by mutableStateOf(PuzzleInfoState.PuzzleState.Solving)
  override var currentMoveNumber = 1
  override var expectedMoves = puzzle.puzzleMoves.size
}
