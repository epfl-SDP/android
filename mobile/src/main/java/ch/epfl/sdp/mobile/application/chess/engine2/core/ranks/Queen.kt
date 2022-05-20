package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine.Delta.Directions

/** A rank implementation for queens. */
object Queen : AttackTowardsRank(Directions.Lines + Directions.Diagonals)
