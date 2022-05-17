package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.Player
import ch.epfl.sdp.mobile.application.chess.engine.rules.Role
import ch.epfl.sdp.mobile.application.chess.engine.rules.denormalize
import ch.epfl.sdp.mobile.application.chess.engine.rules.normalize

/**
 * A decorator of a [Board] of pieces with colors, which returns a [Board] of pieces with roles
 * depending on the current player [Color].
 *
 * @param player the [Color] of the current player.
 * @param board the [Board] of pieces with colors to decorate.
 */
class NormalizedBoardDecorator<P : Player<P>>(
    private val player: P,
    private val board: Board<Piece<P>>,
) : Board<Piece<Role>> {

  override fun get(position: Position): Piece<Role>? {
    return board[player.normalize(position)]?.let { player.normalize(it) }
  }

  override fun set(position: Position, piece: Piece<Role>?): Board<Piece<Role>> {
    val result = board.set(player.normalize(position), piece?.let { player.denormalize(it) })
    return NormalizedBoardDecorator(player, result)
  }

  override fun iterator(): Iterator<Pair<Position, Piece<Role>>> {
    return board.asSequence().map { (pos, piece) -> pos to player.normalize(piece) }.iterator()
  }
}
