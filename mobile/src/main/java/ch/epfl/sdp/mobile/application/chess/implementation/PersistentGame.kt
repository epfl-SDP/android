package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.moves.GameWithRoles
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
          val moves =
              Position.all().flatMap { position ->
                val piece = normalizedBoard[position] ?: return@flatMap emptySequence()
                if (piece.color == Role.Adversary) return@flatMap emptySequence()
                piece.rank.moves(normalizedBoard.withPosition(position))
              }

          val (_, effects) =
              moves.firstOrNull { (action, _) -> action.from == from && action.delta == delta }
                  ?: return@MovePiece this

          val nextBoard = DenormalizedBoardDecorator(nextPlayer, effects.perform(normalizedBoard))

          // TODO : Eventually flatten this ?
          copy(nextPlayer = nextPlayer.other(), board = nextBoard)
        }
}

fun Board<Piece<Role>>.withPosition(
    position: Position,
): GameWithRoles =
    object : GameWithRoles, Board<Piece<Role>> by this {
      override val position = position
    }
