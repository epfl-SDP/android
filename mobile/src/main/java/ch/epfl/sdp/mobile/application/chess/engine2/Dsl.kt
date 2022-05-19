package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta
import ch.epfl.sdp.mobile.application.chess.engine2.core.Position

interface Color

interface BoardScope {
  operator fun get(position: Position): Piece
}

interface Attacked {
  fun isAttacked(position: Position): Boolean
}

interface AttackScope : BoardScope {
  fun attack(position: Position)
}

interface ActionScope : BoardScope, Attacked {
  fun action(position: Position, effect: EffectScope.() -> Unit)
}

interface EffectScope {
  fun insert(position: Position, piece: Piece)
  fun remove(from: Position): Piece
}

fun EffectScope.move(from: Position, to: Position) {
  val removed = remove(from)
  if (removed.isUndefined) return
  insert(to, removed)
}

interface Rank {

  /**
   * Returns all the attacks that a piece with the given [Rank] may perform, considering that it
   * starts at the provided [Position] and has the given [Color].
   *
   * @receiver the [AttackScope] which lets the [Rank] declare its attacks.
   * @param color the [Color] of the piece.
   * @param position the [Position] of the piece.
   */
  fun AttackScope.attacks(color: Color, position: Position)

  // TODO : Document this.
  fun ActionScope.actions(color: Color, position: Position)

  /**
   * Adds all the [attacks] to the [actions], by moving the pieces and eating the attacked pieces.
   *
   * @receiver the [ActionScope] which lets the [Rank] declare its actions.
   * @param color the [Color] of the piece.
   * @param position the [Position] of the piece.
   */
  fun ActionScope.likeAttacks(color: Color, position: Position) {
    val from = position
    val scope =
        object : AttackScope, BoardScope by this {
          override fun attack(position: Position) =
              action(position) { move(from = from, to = position) }
        }
    with(scope) { attacks(color, position) }
  }
}

fun AttackScope.attackTowards(direction: Delta, color: Color, position: Position) {
  var step = direction
  while (true) {
    val next = position + step
    if (!next.inBounds) return
    val existing = get(next)
    if (!existing.isUndefined && existing.color == color) return
    attack(next)
    if (!existing.isUndefined) return
    step += direction
  }
}

abstract class DirectionalRank(private val directions: List<Delta>) : Rank {

  override fun AttackScope.attacks(color: Color, position: Position) {
    for (direction in directions) {
      attackTowards(direction, color, position)
    }
  }

  override fun ActionScope.actions(color: Color, position: Position) = likeAttacks(color, position)
}

private val N = Delta(0, -1)
private val S = Delta(0, 1)

private val E = Delta(1, 0)
private val NE = N + E
private val SE = S + E

private val W = Delta(-1, 0)
private val NW = N + W
private val SW = S + W

private val LinesDirections = listOf(E, S, W, N)
private val DiagonalsDirections = listOf(NE, SE, NW, SW)

object Rook : DirectionalRank(LinesDirections)

object Bishop : DirectionalRank(DiagonalsDirections)

object Queen : DirectionalRank(LinesDirections + DiagonalsDirections)
