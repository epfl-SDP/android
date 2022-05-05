package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation

interface Puzzle {
  val uid: String
  val boardSnapshot: FenNotation.BoardSnapshot
  val puzzleMoves: List<Action>
  val elo: Int
}

/** Creates an empty [Puzzle]. */
fun Puzzle(): Puzzle =
    object : Puzzle {
      override val uid = "Error"
      override val boardSnapshot =
          FenNotation.BoardSnapshot(
              board = buildBoard {},
              playing = Color.White,
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
