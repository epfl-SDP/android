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

  override fun AttackScope.attacks(color: Color, position: Position) {
    val direction = direction(color)

    attack(position + direction + CardinalPoints.E)
    attack(position + direction + CardinalPoints.W)
  }

  override fun ActionScope.actions(color: Color, position: Position) {
    val moveUpTarget = position + direction(color)
    if (get(moveUpTarget).isNone) {
      action(at = moveUpTarget) { move(from = position, to = position + direction(color)) }
    }
    // TODO : En-passant
    // TODO : Double-up
    // TODO : Promotion.
  }
}
