package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine2.core.*

object Pawn : Rank {
  override fun AttackScope.attacks(color: Color, position: Position) = Unit
  override fun ActionScope.actions(color: Color, position: Position) = Unit
}
