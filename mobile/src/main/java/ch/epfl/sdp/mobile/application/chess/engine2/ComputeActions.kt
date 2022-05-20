package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.Color as EngineColor
import ch.epfl.sdp.mobile.application.chess.engine.Position as EnginePosition
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action as EngineAction
import ch.epfl.sdp.mobile.application.chess.engine2.core.*

/** Computes all the possible attacks from the player with the given [EngineColor]. */
private fun MutableBoard.computeAttacks(player: Color): (Position) -> Boolean {
  val cells = Array(MutableBoard.Size) { BooleanArray(MutableBoard.Size) }
  val scope =
      object : AttackScope {
        override fun attack(position: Position) {
          if (position.inBounds) cells[position.x][position.y] = true
        }
        override fun get(position: Position): Piece = this@computeAttacks[position]
      }
  forEachPiece { position, piece ->
    val rank = requireNotNull(piece.rank)
    val color = requireNotNull(piece.color)
    if (player == color) {
      with(rank) { scope.attacks(player, position) }
    }
  }
  return { pos -> if (pos.inBounds) cells[pos.x][pos.y] else false }
}

/**
 * Computes all the possible actions for the given [EnginePosition].
 *
 * @param position the [EnginePosition] for which the actions are computed.
 */
fun MutableBoard.computeActions(
    position: EnginePosition,
    player: EngineColor,
): Sequence<EngineAction> = sequence {
  val from = position
  val piece = get(position.toPosition())
  if (!piece.isNone && piece.color == player.toColor()) {
    // TODO : Some super smart logic to perform the effects and see whether they're good or not.
    // TODO : Some object-oriented approach rather than all these wonderful anonymous classes.
    val computedAttacks = computeAttacks(player.other().toColor())
    val attacked = Attacked { position -> computedAttacks(position) }
    val actions = mutableListOf<EngineAction>()
    val rank = requireNotNull(piece.rank)
    val scope =
        object : ActionScope, Attacked by attacked {
          override fun action(at: Position, effect: EffectScope.() -> Unit) {
            actions.add(EngineAction.Move(from, EnginePosition(at.x, at.y)))
          }
          override fun get(position: Position): Piece = this@computeActions[position]
        }
    with(rank) { scope.actions(player.toColor(), position.toPosition()) }
    yieldAll(actions)
  }
}
