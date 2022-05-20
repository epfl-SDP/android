package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine.Delta

/** A rank implementation for bishops. */
object Bishop : AttackTowardsRank(Delta.Directions.Diagonals)
