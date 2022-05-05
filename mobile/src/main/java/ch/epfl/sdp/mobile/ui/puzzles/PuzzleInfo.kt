package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Composable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color

interface PuzzleInfo {
  val uid: String
  val elo: Int
  val playerColor: Color
  val icon: @Composable (() -> Unit)
}
