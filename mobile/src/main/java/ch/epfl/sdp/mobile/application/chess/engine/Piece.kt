package ch.epfl.sdp.mobile.application.chess.engine

/**
 * A class representing a chess piece, with its color and rank.
 *
 * When checking for equality, pieces should first check for their [color] and [rank], and if they
 * are equal, the [id] should be compared as well. A [Board] may contain some pieces with the same
 * [id] but different colors and ranks, but each triplet ([Color], [Rank], [id]) will be unique.
 *
 * @param Color type of the color of a [Piece].
 * @param color the [Color] of the player who owns the piece.
 * @param rank the rank indicating the abilities of the [Piece].
 * @param id the unique identifier for this piece, used for disambiguation.
 */
data class Piece<Color>(
    val color: Color,
    val rank: Rank,
    val id: PieceIdentifier,
)
