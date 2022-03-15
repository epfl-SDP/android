package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

@Stable
interface ChessBoardState<Identifier> {
  enum class Rank {
    King,
    Queen,
    Rook,
    Bishop,
    Knight,
    Pawn,
  }

  data class Position(val x: Int, val y: Int)

  enum class Color {
    Black,
    White,
  }

  data class Piece<out Identifier>(
      val id: Identifier,
      val rank: Rank,
      val color: Color,
  )

  val pieces: Map<Position, Piece<Identifier>>

  val dragEnabled: Boolean

  fun onDropPiece(piece: Piece<Identifier>, startPosition: Position, endPosition: Position)
}
