package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Color
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position
import ch.epfl.sdp.mobile.application.chess.moves.Role
import ch.epfl.sdp.mobile.application.chess.moves.Role.Adversary
import ch.epfl.sdp.mobile.application.chess.moves.Role.Allied

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
}

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
}
