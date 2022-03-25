package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.Color.Black
import ch.epfl.sdp.mobile.application.chess.Color.White
import ch.epfl.sdp.mobile.application.chess.Rank.*
import ch.epfl.sdp.mobile.application.chess.implementation.BoardBuilder
import ch.epfl.sdp.mobile.application.chess.implementation.PersistentGame
import ch.epfl.sdp.mobile.application.chess.implementation.PersistentPieceIdentifier
import ch.epfl.sdp.mobile.application.chess.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.rules.Action
import kotlinx.collections.immutable.persistentListOf

/**
 * An interface representing the current [Game], which contains a [Board] of pieces and on which
 * some [NextStep] should be performed to move forward for the chess game.
 */
interface Game {

  /**
   * The previous state of the [Game], as well as the [Action] that was performed to obtain the
   * current state. If this is the initial [Game] state, and empty [Pair] is returned.
   */
  val previous: Pair<Game, Action>?

  /** The current [Board], which contains some pieces and should be rendered to the user. */
  val board: Board<Piece<Color>>

  /** Represents the [NextStep] that must be performed on this [Game]. */
  val nextStep: NextStep

  /**
   * Returns a [Sequence] of the possible [Action] for the provided [Position]. If the [Position]
   * does not have a [Piece] or it's not this player's turn, the resulting sequence might be empty.
   *
   * @param position the position for which the available [Action]s are queried.
   * @return the [Sequence] of available actions.
   */
  fun actions(position: Position): Sequence<Action>

  companion object {

    /** Creates a new [Game], with the standard starting positions for both players. */
    fun create(): Game =
        buildGame(White) {
          var id = PersistentPieceIdentifier(0)

          /** Populates a [row] with all the pieces of a given [color]. */
          fun populateSide(row: Int, color: Color) {
            set(Position(0, row), Piece(color, Rook, id++))
            set(Position(1, row), Piece(color, Knight, id++))
            set(Position(2, row), Piece(color, Bishop, id++))
            set(Position(3, row), Piece(color, Queen, id++))
            set(Position(4, row), Piece(color, King, id++))
            set(Position(5, row), Piece(color, Bishop, id++))
            set(Position(6, row), Piece(color, Knight, id++))
            set(Position(7, row), Piece(color, Rook, id++))
          }

          // Populate the pieces.
          populateSide(0, Black)
          populateSide(7, White)

          // Populate the pawns.
          repeat(Board.Size) { column ->
            set(Position(column, 1), Piece(Black, Pawn, id++))
            set(Position(column, 6), Piece(White, Pawn, id++))
          }
        }
  }
}

/**
 * Builds a new [Game] using the provided [BoardBuilder] to prepare the board.
 *
 * @param nextPlayer the color of the first player to play.
 * @param block the scope in which the board is populated.
 */
fun buildGame(
    nextPlayer: Color,
    block: BoardBuilder<Piece<Color>>.() -> Unit,
): Game =
    PersistentGame(
        previous = null,
        nextPlayer = nextPlayer,
        boards = persistentListOf(buildBoard(block)),
    )
