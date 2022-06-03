package ch.epfl.sdp.mobile.application.chess.engine

/**
 * A way for pieces to be uniquely identified. This will let the rendering system smoothly animate
 * between board positions, since pieces with the same stable ids can have their positions
 * interpolated.
 *
 * @property value the backing [Int], in which a piece identifier is encoded.
 */
@JvmInline
value class PieceIdentifier(val value: Int) : Comparable<PieceIdentifier> {

  override operator fun compareTo(other: PieceIdentifier): Int = value.compareTo(other.value)

  /** Returns an incremented [PieceIdentifier]. */
  operator fun inc(): PieceIdentifier {
    return PieceIdentifier(value + 1)
  }
}
