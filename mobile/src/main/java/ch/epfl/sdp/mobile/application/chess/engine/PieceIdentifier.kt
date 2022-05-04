package ch.epfl.sdp.mobile.application.chess.engine

/**
 * A way for pieces to be uniquely identified. This will let the rendering system smoothly animate
 * between board positions, since pieces with the same stable ids can have their positions
 * interpolated.
 */
data class PieceIdentifier(private val id: Int) : Comparable<PieceIdentifier> {

  override operator fun compareTo(other: PieceIdentifier): Int = id.compareTo(other.id)

  /** Returns an incremented [PieceIdentifier]. */
  operator fun inc(): PieceIdentifier {
    return PieceIdentifier(id + 1)
  }
}
