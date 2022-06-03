package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Action.Companion.Move
import ch.epfl.sdp.mobile.application.chess.engine.Action.Companion.Promote
import ch.epfl.sdp.mobile.application.chess.engine.rules.ActionScope
import ch.epfl.sdp.mobile.application.chess.engine.rules.Attacked
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect

/**
 * An implementation of [ActionScope] which populates a list of [Action] and [Effect].
 *
 * @property actions the populated list of actions.
 * @property from the position for which the actions are queried.
 * @property board the initial [MutableBoard].
 * @property history the sequence of past board positions.
 * @param attacked the currently [Attacked] positions.
 */
class MutableBoardActionScope(
    private val actions: MutableList<Pair<Action, Effect>>,
    private val from: Position,
    private val board: MutableBoard,
    private val history: Sequence<Board<Piece<Color>>>,
    attacked: Attacked,
) : ActionScope, Attacked by attacked {

  override fun get(position: Position): MutableBoardPiece = board[position]

  override fun getHistorical(position: Position): Sequence<MutableBoardPiece> =
      history.map { it[position].toMutableBoardPiece() }

  override fun move(at: Position, effect: Effect) {
    if (at.inBounds) {
      actions.add(Move(from, at) to effect)
    }
  }

  override fun promote(at: Position, rank: Rank, effect: Effect) {
    if (at.inBounds) {
      actions.add(Promote(from, at, rank) to effect)
    }
  }
}
