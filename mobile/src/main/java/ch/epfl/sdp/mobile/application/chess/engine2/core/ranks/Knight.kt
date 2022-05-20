package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.E
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.N
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.S
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.W

/** All the directions in which a knight is allowed to jump. */
private val KnightDirections =
    listOf(
        N + N + E,
        N + E + E,
        S + E + E,
        S + S + E,
        S + S + W,
        S + W + W,
        N + W + W,
        N + N + W,
    )

/** A rank implementation for knights. */
object Knight : AttackRank(KnightDirections)
