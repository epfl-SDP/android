package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.BoardSnapshot

/**
 * Represents a complete [Puzzle] TODO: Will definitely change in the "Functional Puzzle Game"
 * feature
 */
interface Puzzle {
  /** The uid of the [Puzzle] */
  val uid: String

  /** The [BoardSnapshot] of the [Puzzle] */
  val boardSnapshot: BoardSnapshot

  /**
   * The list of [Action] to be played by the "computer" and the player. The first action is the
   * last moved played before the player starts the [Puzzle]
   */
  val puzzleMoves: List<Action>

  /** The elo/rank (difficulty) of the [Puzzle] */
  val elo: Int
}

/** Creates an empty [Puzzle]. */
fun Puzzle(): Puzzle =
    object : Puzzle {
      override val uid = "Error"
      override val boardSnapshot =
          BoardSnapshot(
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