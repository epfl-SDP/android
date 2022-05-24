package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.AttackScope
import ch.epfl.sdp.mobile.application.chess.engine.rules.Attacked

/**
 * An implementation of [Attacked] which computes all the attacks performed by the player of color
 * [Color] on the given [MutableBoard].
 *
 * @param board the [MutableBoard] for which the attacks are computed.
 * @param player the [Color] of the attacking player.
 */
class MutableBoardAttacked(
    private val board: MutableBoard,
    player: Color,
) : Attacked, AttackScope {

  /** An [Array] of [BooleanArray] with the cells and whether they're attacked or not. */
  private val cells = Array(MutableBoard.Size) { BooleanArray(MutableBoard.Size) }

  init {
    board.forEachPiece { position, piece ->
      val rank = requireNotNull(piece.rank)
      val color = requireNotNull(piece.color)
      if (player == color) {
        with(rank) { attacks(player, position) }
      }
    }
  }

  override fun isAttacked(position: Position): Boolean =
      position.inBounds && cells[position.x][position.y]
  override fun get(position: Position) = board[position]
  override fun attack(position: Position) {
    if (position.inBounds) cells[position.x][position.y] = true
  }
}
