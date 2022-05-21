package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Action.Companion.Move
import ch.epfl.sdp.mobile.application.chess.engine.Action.Companion.Promote
import ch.epfl.sdp.mobile.application.chess.engine.rules.ActionScope
import ch.epfl.sdp.mobile.application.chess.engine.rules.Attacked
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect

/**
 * Returns true iff the [player] with the given color is currently in check.
 *
 * @receiver the current [MutableBoard] instance.
 * @param player the [Color] of the current player.
 * @return true iff the player is in check.
 */
fun MutableBoard.inCheck(player: Color): Boolean {
  val attacks = MutableBoardAttacked(this, player.other())
  val king = findKing(player)
  return attacks.isAttacked(king)
}

/**
 * Returns true iff the [player] with the given color may perform any move which will result in them
 * not being in check.
 *
 * @receiver the current [MutableBoard] instance.
 * @param player thee [Color] of the current player.
 * @param history the history of the previous board sequences.
 */
fun MutableBoard.hasAnyMoveAvailable(
    player: Color,
    history: Sequence<Board<Piece<Color>>>,
) = anyPiece { position, piece ->
  piece.color == player && !actions(position, player, history).none()
}

// TODO : Document this.
/**
 * Computes all the possible actions for the given [EnginePosition].
 *
 * @param position the [EnginePosition] for which the actions are computed.
 */
fun MutableBoard.actions(
    position: Position,
    player: Color,
    history: Sequence<Board<Piece<Color>>>,
): Sequence<Pair<Action, Effect>> = sequence {
  val from = position
  val piece = get(position)
  if (!piece.isNone && piece.color == player) {
    val attacked = MutableBoardAttacked(this@actions, player.other())
    val actions = mutableListOf<Pair<Action, Effect>>()
    val rank = requireNotNull(piece.rank)
    // TODO : Extract this to a class.
    val scope =
        object : ActionScope, Attacked by attacked {
          override fun move(at: Position, effect: Effect) {
            if (at.inBounds) {
              actions.add(Move(from, at) to effect)
            }
          }
          override fun promote(at: Position, rank: Rank, effect: Effect) {
            if (at.inBounds) {
              actions.add(Promote(from, at, rank) to effect)
            }
          }
          override fun get(position: Position) = this@actions[position]
          override fun getHistorical(position: Position) =
              history.map { it[position].toMutableBoardPiece() }
        }
    with(rank) { scope.actions(player, position) }

    val boardScope = MutableBoardScope(this@actions)

    actions.removeAll { (_, effect) ->
      boardScope.withSave { board ->
        effect()
        val adversaryAttacks = MutableBoardAttacked(board, player.other())
        adversaryAttacks.isAttacked(board.findKing(player))
      }
    }

    yieldAll(actions)
  }
}

/**
 * Returns the [Position] of the king for the given [player] color.
 *
 * @receiver the [MutableBoard] on which the king is searched.
 * @param player the [Color] of the searched king.
 * @return the position of the king.
 */
private fun MutableBoard.findKing(player: Color): Position {
  forEachPiece { position, piece ->
    if (piece.rank == Rank.King && piece.color == player) return position
  }
  error("A MutableBoard should always have a king.")
}
