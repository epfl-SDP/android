package ch.epfl.sdp.mobile.application.chess.engine2.core.ranks

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Rank as RankType
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

  /** Returns the end row of a [Pawn] given their [Color]. */
  private fun endRow(color: Color): Int =
      when (color) {
        Color.Black -> 7
        Color.White -> 0
      }

  override fun AttackScope.attacks(color: Color, position: Position) {
    val direction = direction(color)

    attack(position + direction + CardinalPoints.E)
    attack(position + direction + CardinalPoints.W)
  }

  /**
   * Attempts to move or promote the pawn, depending on which position it's targeting.
   *
   * @param color the [Color] of the player.
   * @param at the target [Position].
   * @param effect the [Effect] to be applied.
   */
  private fun ActionScope.moveOrPromote(color: Color, at: Position, effect: Effect) {
    if (endRow(color) == at.y) {
      val choices =
          listOf(
              Bishop to RankType.Bishop,
              Knight to RankType.Knight,
              Queen to RankType.Queen,
              Rook to RankType.Rook,
          )
      for ((rank, rankType) in choices) {
        // For each possible rank, move the pawn, then remove it and finally replace it with an
        // other piece with the same id.
        promote(at, rankType) {
          effect()
          val pawn = remove(at)
          insert(at, Piece(pawn.id, rank, color))
        }
      }
    } else {
      // Move without promotion.
      move(at, effect)
    }
  }

  /** Moves the pawn up, assuming there is a free square above it. */
  private fun ActionScope.singleUp(color: Color, position: Position) {
    val target = position + direction(color)
    if (get(target).isNone) {
      moveOrPromote(color = color, at = target) { move(from = position, to = target) }
    }
  }

  /** Moves the pawn up by two squares, assuming that the two squares above are empty. */
  private fun ActionScope.doubleUp(color: Color, position: Position) {
    if (startRow(color) != position.y) return
    val target = position + (direction(color) * 2)
    if (get(position + direction(color)).isNone && get(target).isNone) {
      move(at = target) { move(from = position, to = target) }
    }
  }

  /** Takes the pieces to the right and to the left above the pawn. */
  private fun ActionScope.sideTakes(color: Color, position: Position) {
    for (sideDelta in listOf(CardinalPoints.E, CardinalPoints.W)) {
      val target = position + direction(color) + sideDelta
      val piece = get(target)
      if (!piece.isNone && piece.color != color) {
        moveOrPromote(color = color, at = target) { move(from = position, to = target) }
      }
    }
  }

  /** Takes the adversary pawns en-passant. */
  private fun ActionScope.enPassant(color: Color, position: Position) {
    for (sideDelta in listOf(CardinalPoints.E, CardinalPoints.W)) {
      val target = position + direction(color) + sideDelta
      val adversaryPosition = position + sideDelta
      val adversary = get(adversaryPosition)
      if (adversary.isNone || adversary.color == color || adversary.rank != Pawn) continue
      val adversaryStart = target + direction(color)
      if (!adversaryStart.inBounds) continue
      if (getHistorical(adversaryStart).drop(2).any { it != adversary }) continue
      move(target) {
        move(position, target)
        remove(adversaryPosition)
      }
    }
  }

  override fun ActionScope.actions(color: Color, position: Position) {
    singleUp(color, position)
    doubleUp(color, position)
    sideTakes(color, position)
    enPassant(color, position)
  }
}
