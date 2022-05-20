package ch.epfl.sdp.mobile.application.chess.engine2.core

/** A scope which provides access to an underlying board. */
interface BoardScope {

  /** Returns the [Piece] at the given [Position]. */
  operator fun get(position: Position): Piece
}

/** A scope which provides access to the attacked positions from the opponent. */
interface Attacked {

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
    if (!existing.isUndefined && existing.color == color) return
    attack(next)
    if (!existing.isUndefined) return
    step += direction
  }
}

/**
 * A scope which is used to declare that an action can be performed if the given [Position] is
 * clicked. Multiple actions may be performed on the same final [Position].
 */
interface ActionScope : BoardScope, Attacked {

  /**
   * Declares that an action is available for the given [Position].
   *
   * @param position the [Position] or the action.
   * @param effect the effects to be performed when the action is chosen.
   */
  fun action(position: Position, effect: EffectScope.() -> Unit)
}

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
  if (removed.isUndefined) return
  insert(to, removed)
}
