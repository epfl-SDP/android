package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Delta.Directions

/** A rank implementation for rooks. */
object RookRules : AttackTowardsRules(Directions.Lines)
