package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.runtime.Composable
import ch.epfl.sdp.mobile.application.chess.engine.Color.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.BoardSnapshot
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
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
  val icon: @Composable (() -> Unit)
}

class SnapshotPuzzle(
    override val uid: String,
    override val boardSnapshot: BoardSnapshot,
    override val puzzleMoves: List<Action>,
    override val elo: Int,
) : Puzzle

/** Creates an empty [Puzzle]. */
fun Puzzle(): Puzzle =
    object : Puzzle {
      override val uid = "Error"
      override val boardSnapshot =
          BoardSnapshot(
              board = buildBoard {},
              playing = White,
              castlingRights =
                  FenNotation.CastlingRights(
                      kingSideWhite = false,
                      queenSideWhite = false,
                      kingSideBlack = false,
                      queenSideBlack = false,
                  ),
              enPassant = null,
              halfMoveClock = -1,
              fullMoveClock = -1,
          )
      override val puzzleMoves = emptyList<Action>()
      override val elo = -1
    }
