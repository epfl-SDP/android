package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.moves.Role

/**
 * A persistent implementation of a [Game], which contains some information about the current board
 * positions, the past moves that were performed and the possible next steps.
 *
 * @param nextPlayer the [Color] of the next player to play.
 */
data class PersistentGame(
    val nextPlayer: Color,
    override val board: Board<Piece<Color>>,
) : Game {

  override val nextStep: NextStep
    get() =
        NextStep.MovePiece(nextPlayer) { from, delta ->
          val normalizedBoard = NormalizedBoardDecorator(nextPlayer, board)
          val normalizedFrom = nextPlayer.normalize(from)
          val normalizedDelta = nextPlayer.normalize(delta) // TODO : Normalize delta.
          val piece = normalizedBoard[normalizedFrom] ?: return@MovePiece this
          if (piece.color == Role.Adversary) return@MovePiece this
          val moves = piece.rank.moves(normalizedBoard, normalizedFrom)

          val (_, effects) =
              moves.firstOrNull { (action, _) ->
                action.from == normalizedFrom && action.delta == normalizedDelta
              }
                  ?: return@MovePiece this

          val nextBoard = DenormalizedBoardDecorator(nextPlayer, effects.perform(normalizedBoard))

          // TODO : Eventually flatten this ?
          copy(nextPlayer = nextPlayer.other(), board = nextBoard)
        }
}