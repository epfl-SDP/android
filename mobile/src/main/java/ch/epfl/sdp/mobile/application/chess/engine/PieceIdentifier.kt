package ch.epfl.sdp.mobile.application.chess.engine

/**
 * A way for pieces to be uniquely identified. This will let the rendering system smoothly animate
 * between board positions, since pieces with the same stable ids can have their positions
 * interpolated.
 */
interface PieceIdentifier
