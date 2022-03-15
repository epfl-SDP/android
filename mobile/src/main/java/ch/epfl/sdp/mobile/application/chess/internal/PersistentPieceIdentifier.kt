package ch.epfl.sdp.mobile.application.chess.internal

import ch.epfl.sdp.mobile.application.chess.PieceIdentifier

/**
 * An implementation of [PieceIdentifier] which simply contains an internal unique identifier, and
 * is stable.
 *
 * @param id the actual identifier of the piece.
 */
data class PersistentPieceIdentifier(private val id: Int) : PieceIdentifier
