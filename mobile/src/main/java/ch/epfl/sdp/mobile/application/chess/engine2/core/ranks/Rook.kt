package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine.Delta.Directions

/** A rank implementation for rooks. */
object Rook : AttackTowardsRank(Directions.Lines)
