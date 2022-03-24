package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.rules.Action
import ch.epfl.sdp.mobile.application.chess.rules.Moves
import ch.epfl.sdp.mobile.application.chess.rules.Role

/**
 * A persistent implementation of a [Game], which contains some information about the current board
 * positions, the past moves that were performed and the possible next steps.
 *
 * @param nextPlayer the [Color] of the next player to play.
 */
data class PersistentGame(
    override val previous: Pair<PersistentGame, Action>?,
    val nextPlayer: Color,
    override val board: Board<Piece<Color>>,
) : Game {

  /**
   * An implementation of a [BoardWithHistory] which delegates retrieving information to the current
   * [board] or the [previous] [PersistentGame].
   *
   * @param player the [Color] for which the board should be normalized.
   */
  private inner class PersistentNormalizedGameBoardWithHistory(
      private val player: Color,
  ) : BoardWithHistory<Piece<Role>>, Board<Piece<Role>> by NormalizedBoardDecorator(player, board) {

    override val previousBoardWithHistory: BoardWithHistory<Piece<Role>>?
      get() = previous?.let { (game, _) -> game.PersistentNormalizedGameBoardWithHistory(player) }
  }

  override val nextStep: NextStep
    get() =
        NextStep.MovePiece(nextPlayer) { from, delta ->
          val normalizedBoard = PersistentNormalizedGameBoardWithHistory(nextPlayer)
          val normalizedFrom = nextPlayer.normalize(from)
          val normalizedDelta = nextPlayer.normalize(delta)
          val moves = normalizedMoves(from)

          val (action, effects) =
              moves.firstOrNull { (action, _) ->
                action.from == normalizedFrom && action.delta == normalizedDelta
              }
                  ?: return@MovePiece this

          val nextBoard = DenormalizedBoardDecorator(nextPlayer, effects.perform(normalizedBoard))

          // TODO : Eventually flatten this ?
          copy(
              previous = this to nextPlayer.normalize(action),
              nextPlayer = nextPlayer.other(),
              board = nextBoard,
          )
        }

  override fun actions(position: Position): Sequence<Action> {
    return normalizedMoves(position).map { it.first }.map {
      Action(nextPlayer.normalize(it.from), nextPlayer.normalize(it.delta))
    }
  }

  /**
   * Returns all the [Moves] which are available for the given position.
   *
   * @param position the [Position] for which the moves are queried.
   * @return the [Moves] for the position.
   */
  private fun normalizedMoves(position: Position): Moves {
    val normalizedBoard = PersistentNormalizedGameBoardWithHistory(nextPlayer)
    val normalizedFrom = nextPlayer.normalize(position)
    val piece = normalizedBoard[normalizedFrom] ?: return emptySequence()
    if (piece.color == Role.Adversary) return emptySequence()
    return piece.rank.moves(normalizedBoard, normalizedFrom)
  }
}
