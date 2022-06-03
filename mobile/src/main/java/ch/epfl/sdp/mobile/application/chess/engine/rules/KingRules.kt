package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Delta.Directions
import ch.epfl.sdp.mobile.application.chess.engine.Position

/** A rank implementation for kings. */
object KingRules : AttackRules(Directions.Lines + Directions.Diagonals) {

  /**
   * Castles the king towards the right or the left.
   *
   * @param kingPosition the current [Position] of the king.
   * @param rookPosition the current [Position] of the rook.
   */
  private fun ActionScope.castling(kingPosition: Position, rookPosition: Position) {
    val king = get(kingPosition)
    val rook = get(rookPosition)

    // We can't castle if the king is attacked.
    if (isAttacked(kingPosition)) return

    // We can safely assume that if the pieces have always been at these positions, we'll find the
    // right king and the right rook, and their colors will match.
    if (getHistorical(kingPosition).any { it != king } ||
        getHistorical(rookPosition).any { it != rook }) {
      return
    }

    val direction = (rookPosition - kingPosition).sign

    // Look at the two spots to next to the tower and the king. This makes sure that the spots are
    // free for both left and right castling, and we only check for attacks on the king positions.
    for (i in 1..2) {
      val kingTarget = kingPosition + (direction * i)
      val rookTarget = rookPosition - (direction * i)
      if (!get(kingTarget).isNone || !get(rookTarget).isNone) return // There's a piece in the way.
      if (isAttacked(kingTarget)) return // One of the king destinations is in check.
    }

    val kingTarget = kingPosition + (direction * 2)
    val rookTarget = kingTarget - direction
    move(kingTarget) {
      move(kingPosition, kingTarget)
      move(rookPosition, rookTarget)
    }
  }

  override fun ActionScope.actions(color: Color, position: Position) {
    likeAttacks(color, position)
    castling(position, Position(0, position.y)) // Left castling.
    castling(position, Position(7, position.y)) // Right castling.
  }
}
