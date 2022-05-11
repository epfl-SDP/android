package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.application.chess.Puzzle

/** An interface that represents the user interface information about the puzzle. */
@Stable
interface PuzzleInfoState {
  /** The [Puzzle] that is loaded */
  val puzzleInfo: PuzzleInfo

  /** The state of the puzzle */
  var puzzleState: PuzzleState

  var currentMoveNumber: Int // Always start with playing "computer" move

  var expectedMoves: Int
  /** Represents the three possible states for a puzzle */
  enum class PuzzleState {
    Solving,
    Failed,
    Solved,
  }
}
