package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Composable
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color

/** Represents the basic info of a [Puzzle] to display it in a list. */
interface PuzzleInfo {
  /** The [Puzzle]'s uid. */
  val uid: String

  /** The elo/rank (difficulty) of the puzzle. */
  val elo: Int

  /** The [Color] of the player in the [Puzzle]. */
  val playerColor: Color

  /** The icon [Composable] to display next to the [Puzzle] description. */
  val icon: @Composable () -> Unit
}
