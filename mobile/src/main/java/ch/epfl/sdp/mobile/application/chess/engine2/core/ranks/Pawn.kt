package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine2.core.*
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints

/** An implementation of [Rank] which indicates the actions supported a pawn. */
object Pawn : Rank {

  /** Returns the direction towards which a [Pawn] with the given [Color] may move. */
  private fun direction(color: Color): Delta =
      when (color) {
        Color.Black -> CardinalPoints.S
        Color.White -> CardinalPoints.N
      }

  /** Returns the start row of a [Pawn] given their [Color]. */
  private fun startRow(color: Color): Int =
      when (color) {
        Color.Black -> 1
        Color.White -> 6
      }

  override fun AttackScope.attacks(color: Color, position: Position) {
    val direction = direction(color)

    attack(position + direction + CardinalPoints.E)
    attack(position + direction + CardinalPoints.W)
  }

  /** Moves the pawn up, assuming there is a free square above it. */
  private fun ActionScope.singleUp(color: Color, position: Position) {
    val target = position + direction(color)
    if (get(target).isNone) {
      action(at = target) { move(from = position, to = target) }
    }
  }

  /** Moves the pawn up by two squares, assuming that the two squares above are empty. */
  private fun ActionScope.doubleUp(color: Color, position: Position) {
    if (startRow(color) != position.y) return
    val target = position + (direction(color) * 2)
    if (get(position + direction(color)).isNone && get(target).isNone) {
      action(at = target) { move(from = position, to = target) }
    }
  }

  override fun ActionScope.actions(color: Color, position: Position) {
    singleUp(color, position)
    doubleUp(color, position)
    // TODO : Side-takes.
    // TODO : En-passant
    // TODO : Promotion.
  }
}
