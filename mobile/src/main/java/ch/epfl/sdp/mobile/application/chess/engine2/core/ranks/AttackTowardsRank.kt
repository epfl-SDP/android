package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine2.core.*

/**
 * An implementation of [Rank] which attacks and moves towards a [List] of possible directions.
 *
 * @param directions the [List] of possible directions.
 */
abstract class AttackTowardsRank(private val directions: List<Delta>) : Rank {

  override fun AttackScope.attacks(color: Color, position: Position) {
    for (direction in directions) {
      attackTowards(direction, color, position)
    }
  }

  override fun ActionScope.actions(color: Color, position: Position) = likeAttacks(color, position)
}
