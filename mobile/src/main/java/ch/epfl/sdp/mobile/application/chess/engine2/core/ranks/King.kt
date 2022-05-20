package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.Directions

/** A rank implementation for kings. */
object King : AttackRank(Directions.Lines + Directions.Diagonals) // TODO : Castling.
