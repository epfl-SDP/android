package ch.epfl.sdp.mobile.application.chess

/**
 * A class representing a chess piece, with its color and rank.
 *
 * @param color the [Color] of the player who owns the piece.
 * @param rank the rank indicating the abilities of the [Piece].
 */
data class Piece(val color: Color, val rank: Rank)
