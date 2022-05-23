package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Delta.Directions

/** A rank implementation for queens. */
object QueenRules : AttackTowardsRules(Directions.Lines + Directions.Diagonals)
