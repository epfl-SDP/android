package ch.epfl.sdp.mobile.ui.puzzles

import ch.epfl.sdp.mobile.ui.game.ChessBoardState

interface Puzzle {
  val playerColor: ChessBoardState.Color
  val uid: String
  val moveSet: String
  val playNumber: Int
}

interface PuzzleItem {
  val uid: String
  val playerColor: ChessBoardState.Color
}

/** Creates a [Puzzle] with a given id. */
fun Puzzle(uid: String): Puzzle =
    object : Puzzle {
      override val playerColor = ChessBoardState.Color.White
      override val uid = uid
      override val moveSet = "a2-a4"
      override val playNumber = 0
    }
