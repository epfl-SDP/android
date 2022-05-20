package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine.Rank

/** A scope which provides access to an underlying board. */
interface BoardScope {

  /** Returns the [Piece] at the given [Position]. */
  operator fun get(position: Position): Piece
}

/** A scope which provides access to the history of the pieces present on board. */
interface HistoricalBoardScope : BoardScope {

  /** Returns the [Sequence] of [Piece] that were present at the given position. */
  fun getHistorical(position: Position): Sequence<Piece>
}

/** A scope which provides access to the attacked positions from the opponent. */
fun interface Attacked {

  /** Returns true if the given [Position] is attacked by the opponent. */
  fun isAttacked(position: Position): Boolean
}

/** A scope which is used to declare that a [Position] is attacked. */
interface AttackScope : BoardScope {

  /** Declares that the given [Position] is attacked. */
  fun attack(position: Position)
}

/**
 * Attacks all the pieces in a certain direction.
 *
 * @receiver the [AttackScope] in which the attacks are registered.
 * @param direction the direction of attack.
 * @param color the [Color] of the piece which attacks.
 * @param position the [Position] at which the attacking piece is located.
 */
fun AttackScope.attackTowards(direction: Delta, color: Color, position: Position) {
  var step = direction
  while (true) {
    val next = position + step
    if (!next.inBounds) return
    val existing = get(next)
    if (!existing.isNone && existing.color == color) return
    attack(next)
    if (!existing.isNone) return
    step += direction
  }
}

/**
 * A scope which is used to declare that an action can be performed if the given [Position] is
 * clicked. Multiple actions may be performed on the same final [Position].
 */
interface ActionScope : HistoricalBoardScope, Attacked {

  /**
   * Declares that an action is available for the given [Position].
   *
   * @param at the [Position] or the action.
   * @param effect the effects to be performed when the action is chosen.
   */
  fun move(at: Position, effect: Effect)

  /**
   * Declares that a promotion action is available for the given [Position].
   *
   * @param at the [Position] of the action.
   * @param rank the [Rank] allowed for promotion.
   * @param effect the effects to be performed when the action is chosen.
   */
  fun promote(at: Position, rank: Rank, effect: Effect)
}

/** A typealias representing an [Effect] which will be applied to the board. */
typealias Effect = EffectScope.() -> Unit

/**
 * An implementation of [EffectScope] which uses a [MutableBoard].
 *
 * @param board the underlying [MutableBoard].
 */
private class EffectScopeImpl(private val board: MutableBoard) : EffectScope {
  override fun insert(position: Position, piece: Piece) {
    board[position] = piece
  }
  override fun remove(from: Position): Piece {
    val existing = board[from]
    board.remove(from)
    return existing
  }
}

/**
 * Performs an [Effect] on a [MutableBoard].
 *
 * @receiver the [MutableBoard] on which the effect is applied.
 * @param effect the applied [Effect].
 */
fun MutableBoard.perform(effect: Effect) = effect(EffectScopeImpl(this))

/** A scope which is used to declare an effect to be performed. */
interface EffectScope {

  /** Insets the given [Piece] at the provided [Position]. */
  fun insert(position: Position, piece: Piece)

  /** Removes the [Piece] at the given [Position] and returns it. */
  fun remove(from: Position): Piece
}

/**
 * Moves a piece from an existing [Position] to another [Position]. If no piece was present at the
 * start position, the target [Position] will not be affected.
 *
 * @receiver the [EffectScope] on which the moves are performed.
 * @param from the start [Position].
 * @param to the end [Position].
 */
fun EffectScope.move(from: Position, to: Position) {
  val removed = remove(from)
  if (removed.isNone) return
  insert(to, removed)
}
