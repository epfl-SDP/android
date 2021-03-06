package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.buildGame
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.BoardSnapshot

/** Represents a complete [Puzzle]. */
interface Puzzle {

  /** The uid of the [Puzzle]. */
  val uid: String

  /** The [BoardSnapshot] of the [Puzzle]. */
  val boardSnapshot: BoardSnapshot

  /**
   * The list of [Action] to be played by the "computer" and the player. The first action is the
   * last moved played before the player starts the [Puzzle].
   */
  val puzzleMoves: List<Action>

  /** The elo/rank (difficulty) of the [Puzzle]. */
  val elo: Int
}

/**
 * Creates the base [Game] from the [Puzzle]'s information.
 *
 * @return The corresponding base [Game].
 */
fun Puzzle.baseGame(): Game {
  val baseGame = buildGame(nextPlayer = boardSnapshot.playing, board = boardSnapshot.board)

  val step = baseGame.nextStep as? NextStep.MovePiece ?: return baseGame
  val move = puzzleMoves.firstOrNull() ?: return baseGame

  return step.move(move)
}

/** Creates an empty [Puzzle]. */
fun Puzzle(): Puzzle =
    object : Puzzle {
      override val uid = ""
      override val boardSnapshot =
          BoardSnapshot(
              board = emptyBoard(),
              playing = Color.Black,
              castlingRights =
                  FenNotation.CastlingRights(
                      kingSideWhite = false,
                      queenSideWhite = false,
                      kingSideBlack = false,
                      queenSideBlack = false,
                  ),
              enPassant = null,
              halfMoveClock = 0,
              fullMoveClock = 0,
          )
      override val puzzleMoves = emptyList<Action>()
      override val elo = 0
    }
