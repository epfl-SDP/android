package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Color
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position
import ch.epfl.sdp.mobile.application.chess.rules.Role
import ch.epfl.sdp.mobile.application.chess.rules.Role.Adversary
import ch.epfl.sdp.mobile.application.chess.rules.Role.Allied

/**
 * A decorator of a [Board] of pieces with colors, which returns a [Board] of pieces with roles
 * depending on the current player [Color].
 *
 * @param player the [Color] of the current player.
 * @param board the [Board] of pieces with colors to decorate.
 */
class NormalizedBoardDecorator(
    private val player: Color,
    private val board: Board<Piece<Color>>,
) : Board<Piece<Role>> {

  private fun Piece<Color>.normalize(): Piece<Role> =
      Piece(if (color == player) Allied else Adversary, rank, id)

  private fun Piece<Role>.denormalize(): Piece<Color> =
      Piece(if (color == Allied) player else player.other(), rank, id)

  override fun get(position: Position): Piece<Role>? {
    return board[player.normalize(position)]?.normalize()
  }

  override fun set(position: Position, piece: Piece<Role>?): Board<Piece<Role>> {
    val result = board.set(player.normalize(position), piece?.denormalize())
    return NormalizedBoardDecorator(player, result)
  }

  override fun iterator(): Iterator<Pair<Position, Piece<Role>>> {
    return board.asSequence().map { (pos, piece) -> pos to piece.normalize() }.iterator()
  }
}

/**
 * A decorator of a [Board] of pieces with roles, which returns a [Board] of pieces with colors
 * depending on the current player [Color].
 *
 * @param player the [Color] of the current player.
 * @param board the [Board] of pieces with roles to decorate.
 */
class DenormalizedBoardDecorator(
    private val player: Color,
    private val board: Board<Piece<Role>>,
) : Board<Piece<Color>> {

  private fun Piece<Color>.denormalize(): Piece<Role> =
      Piece(if (color == player) Allied else Adversary, rank, id)

  private fun Piece<Role>.normalize(): Piece<Color> =
      Piece(if (color == Allied) player else player.other(), rank, id)

  override fun get(position: Position): Piece<Color>? {
    return board[player.normalize(position)]?.normalize()
  }

  override fun set(position: Position, piece: Piece<Color>?): Board<Piece<Color>> {
    val result = board.set(player.normalize(position), piece?.denormalize())
    return DenormalizedBoardDecorator(player, result)
  }

  override fun iterator(): Iterator<Pair<Position, Piece<Color>>> {
    return board.asSequence().map { (pos, piece) -> pos to piece.normalize() }.iterator()
  }
}
