package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Position

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
