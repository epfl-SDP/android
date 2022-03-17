package ch.epfl.sdp.mobile.application.chess.internal

import ch.epfl.sdp.mobile.application.chess.Color
import ch.epfl.sdp.mobile.application.chess.Game
import ch.epfl.sdp.mobile.application.chess.NextStep

/**
 * A persistent implementation of a [Game], which contains some information about the current board
 * positions, the past moves that were performed and the possible next steps.
 *
 * @param nextPlayer the [Color] of the next player to play.
 */
data class PersistentGame(
    val nextPlayer: Color,
    override val board: PersistentBoard,
) : Game {

  override val nextStep: NextStep
    get() =
        NextStep.MovePiece(nextPlayer) { from, delta ->
          when (val to = from + delta) {
            null -> this // Invalid move, which we can simply skip.
            else ->
                copy(
                    nextPlayer = nextPlayer.other(),
                    board = board.set(from, null).set(to, board[from]),
                )
          }
        }
}

/** Returns the opposite [Color]. */
private fun Color.other(): Color =
    when (this) {
      Color.Black -> Color.White
      Color.White -> Color.Black
    }
