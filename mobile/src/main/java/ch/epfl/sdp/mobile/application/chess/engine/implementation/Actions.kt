package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*
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
 * @return true iff the player may perform any move.
 */
fun MutableBoard.hasAnyMoveAvailable(
    player: Color,
    history: Sequence<Board<Piece<Color>>>,
) = anyPiece { position, piece ->
  piece.color == player && !actions(position, player, history).none()
}

/**
 * Returns all the pairs of [Action] and [Effect] which are available at the given position.
 *
 * @receiver the current [MutableBoard] instance.
 * @param position the [Position] for which the actions are computed.
 * @param player the color of the player.
 * @param history the sequence of boards that led to the current position.
 * @return a [Sequence] of pairs of actions and effects.
 */
fun MutableBoard.actions(
    position: Position,
    player: Color,
    history: Sequence<Board<Piece<Color>>>,
): Sequence<Pair<Action, Effect>> = sequence {
  val piece = get(position)
  if (piece.color == player) {
    val attacked = MutableBoardAttacked(this@actions, player.other())
    val actions = mutableListOf<Pair<Action, Effect>>()
    val rank = requireNotNull(piece.rank)
    val boardScope = MutableBoardScope(this@actions)

    with(rank) {
      MutableBoardActionScope(
              actions = actions,
              from = position,
              board = this@actions,
              history = history,
              attacked = attacked,
          )
          .actions(player, position)
    }

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
  val (_, position) =
      firstPieceOrNone { _, piece -> piece.rank == Rank.King && piece.color == player }
  return position
}
