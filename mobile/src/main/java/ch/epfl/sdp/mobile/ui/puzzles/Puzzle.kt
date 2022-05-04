package ch.epfl.sdp.mobile.ui.puzzles

import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.BoardSnapshot
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color

interface Puzzle {
  val uid: String
  val boardSnapshot: BoardSnapshot
  val puzzleMoves: List<Action>
  val elo: Int
}

interface PuzzleItem {
  val uid: String
  val elo: Int
  val playerColor: Color
}

class SnapshotPuzzle(
    override val uid: String,
    override val boardSnapshot: BoardSnapshot,
    override val puzzleMoves: List<Action>,
    override val elo: Int,
) : Puzzle
