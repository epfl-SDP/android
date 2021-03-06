package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position

/**
 * An implementation of [Rules] which attacks and moves with a set of possible [Delta].
 *
 * @property directions the [List] of possible directions.
 */
abstract class AttackRules(private val directions: List<Delta>) : Rules {

  override fun AttackScope.attacks(color: Color, position: Position) {
    for (direction in directions) {
      val next = position + direction
      if (next.inBounds) {
        val existing = get(next)
        if (existing.isNone || existing.color != color) {
          attack(next)
        }
      }
    }
  }

  override fun ActionScope.actions(color: Color, position: Position) = likeAttacks(color, position)
}
