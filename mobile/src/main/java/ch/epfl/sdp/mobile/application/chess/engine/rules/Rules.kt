package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.implementation.MutableBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.MutableBoardPiece

/** An interface representing the [Rules] which apply at a given position. */
interface Rules {

  /**
   * Returns all the attacks that a piece with the given [Rank] may perform, considering that it
   * starts at the provided [Position] and has the given [Color].
   *
   * @receiver the [AttackScope] which lets the [Rank] declare its attacks.
   * @param color the [Color] of the piece.
   * @param position the [Position] of the piece.
   */
  fun AttackScope.attacks(color: Color, position: Position)

  /**
   * Returns all the actions that a piece with the given [Rank] may perform, considering that it
   * starts at the provided [Position] and has the given [Color].
   *
   * @receiver the [ActionScope] which lets the [Rank] declare its actions.
   * @param color the [Color] of the piece.
   * @param position the [Position] of the piece.
   */
  fun ActionScope.actions(color: Color, position: Position)

  /**
   * Adds all the [attacks] to the [actions], by moving the pieces and eating the attacked pieces.
   *
   * @receiver the [ActionScope] which lets the [Rank] declare its actions.
   * @param color the [Color] of the piece.
   * @param position the [Position] of the piece.
   */
  fun ActionScope.likeAttacks(color: Color, position: Position) {
    with(AttackScopeAdapter(position, this)) { attacks(color, position) }
  }
}

/**
 * An implementation of [AttackScope] which delegates all the attacks to an [ActionScope], moving
 * the pieces on attacked positions.
 *
 * @param from the start [Position].
 * @param actionScope the [ActionScope] to which the moves are delegated.
 */
private class AttackScopeAdapter(
    private val from: Position,
    private val actionScope: ActionScope,
) : AttackScope, BoardScope by actionScope {

  override fun attack(
      position: Position,
  ) = with(actionScope) { move(position) { move(from = from, to = position) } }
}

// SCOPE DEFINITIONS

/** A scope which provides access to an underlying board. */
interface BoardScope {

  /**
   * Returns the [MutableBoardPiece] at the given [Position].
   *
   * @param position the [Position] for which the current piece is queried.
   * @return the [MutableBoardPiece] at this [position].
   */
  operator fun get(position: Position): MutableBoardPiece
}

/** A scope which provides access to the history of the pieces present on board. */
interface HistoricalBoardScope : BoardScope {

  /**
   * Returns the [Sequence] of [MutableBoardPiece] that were present at the given position.
   *
   * @param position the [Position] for which the history is queried.
   * @return the [Sequence] of [MutableBoardPiece] which form the history of this [position].
   */
  fun getHistorical(position: Position): Sequence<MutableBoardPiece>
}

/** A scope which provides access to the attacked positions from the opponent. */
fun interface Attacked {

  /**
   * Returns true if the given [Position] is attacked by the opponent.
   *
   * @param position the position which is checked.
   * @return true iff [position] is threatened.
   */
  fun isAttacked(position: Position): Boolean
}

/** A scope which is used to declare that a [Position] is attacked. */
interface AttackScope : BoardScope {

  /**
   * Declares that the given [Position] is attacked.
   *
   * @param position the attacked [Position].
   */
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
  override fun insert(position: Position, piece: MutableBoardPiece) {
    board[position] = piece
  }
  override fun remove(from: Position): MutableBoardPiece {
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
  fun insert(position: Position, piece: MutableBoardPiece)

  /** Removes the [Piece] at the given [Position] and returns it. */
  fun remove(from: Position): MutableBoardPiece
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
