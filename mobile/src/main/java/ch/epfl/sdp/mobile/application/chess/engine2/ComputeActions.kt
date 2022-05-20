package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.Color as EngineColor
import ch.epfl.sdp.mobile.application.chess.engine.Position as EnginePosition
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action as EngineAction
import ch.epfl.sdp.mobile.application.chess.engine2.core.*
import ch.epfl.sdp.mobile.application.chess.engine2.core.ranks.King

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

fun MutableBoard.inCheck(player: EngineColor): Boolean {
  val computedAttacks = computeAttacks(player.other().toColor())
  val king = findKing(player.toColor())
  return computedAttacks(king)
}

fun MutableBoard.hasAnyMoveAvailable(player: EngineColor): Boolean {
  forEachPiece { (x, y), piece ->
    if (piece.color == player.toColor()) {
      val actions = computeActions(EnginePosition(x, y), player)
      val count = actions.count()
      if (!actions.none()) {
        println("FOUND AT LEAST ONE ACTION")
        return true
      } else {
        println("FOUND $count ACTIONS")
      }
    }
  }
  println("FOUND NOTHING")
  return false
}

/**
 * Computes all the possible actions for the given [EnginePosition].
 *
 * @param position the [EnginePosition] for which the actions are computed.
 */
fun MutableBoard.computeActions(
    position: EnginePosition,
    player: EngineColor,
): Sequence<Pair<EngineAction, Effect>> = sequence {
  val from = position
  val piece = get(position.toPosition())
  if (!piece.isNone && piece.color == player.toColor()) {
    // TODO : Some super smart logic to perform the effects and see whether they're good or not.
    // TODO : Some object-oriented approach rather than all these wonderful anonymous classes.
    val computedAttacks = computeAttacks(player.other().toColor())
    val attacked = Attacked { position -> computedAttacks(position) }
    val actions = mutableListOf<Pair<EngineAction, Effect>>()
    val rank = requireNotNull(piece.rank)
    val scope =
        object : ActionScope, Attacked by attacked {
          override fun action(at: Position, effect: Effect) {
            actions.add(EngineAction.Move(from, EnginePosition(at.x, at.y)) to effect)
          }
          override fun get(position: Position): Piece = this@computeActions[position]
        }
    with(rank) { scope.actions(player.toColor(), position.toPosition()) }

    val boardScope = MutableBoardScope(this@computeActions)

    actions.removeAll { (_, effect) ->
      boardScope.withSave { board ->
        effect()
        val adversaryAttacks = board.computeAttacks(player.other().toColor())
        adversaryAttacks(board.findKing(player.toColor()))
      }
    }

    yieldAll(actions)
  }
}

/**
 * Returns the [Position] of the king for the given [player] color.
 *
 * @receiver the [MutableBoard] on which the king is searched.
 * @param player the [Color] of the searched king.
 * @return the position of the king.
 */
private fun MutableBoard.findKing(player: Color): Position {
  forEachPiece { position, piece ->
    if (piece.rank == King && piece.color == player) return position
  }
  error("A MutableBoard should always have a king.")
}
