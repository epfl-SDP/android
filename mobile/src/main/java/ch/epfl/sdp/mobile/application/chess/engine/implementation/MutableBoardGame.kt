package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.perform

/**
 * An implementation of a [Game] which uses a [MutableBoard] under-the-hood to compute the available
 * moves.
 *
 * @param previous the previous [MutableBoardGame], if there's any.
 * @param mutableBoard the [MutableBoard] representing the current position.
 * @param nextPlayer the [Color] of the next player to play.
 */
data class MutableBoardGame(
    override val previous: Pair<MutableBoardGame, Action>?,
    private val mutableBoard: MutableBoard,
    private val nextPlayer: Color,
) : Game {

  override val board = mutableBoard.toBoard()

  /** A [Sequence] of all the [Board] taken from the [previous] board. */
  private val history = sequence {
    var current: MutableBoardGame? = this@MutableBoardGame
    while (current != null) {
      yield(current.board)
      current = current.previous?.first
    }
  }

  override val nextStep: NextStep

  init {
    val hasActions = mutableBoard.hasAnyMoveAvailable(nextPlayer, history)
    val inCheck = mutableBoard.inCheck(nextPlayer)

    nextStep =
        if (!hasActions) {
          if (inCheck) {
            NextStep.Checkmate(nextPlayer.other())
          } else {
            NextStep.Stalemate
          }
        } else {
          NextStep.MovePiece(nextPlayer, inCheck) { action ->
            val (_, effect) =
                mutableBoard.actions(action.from, nextPlayer, history).firstOrNull { (it, _)
                  ->
                  action == it
                }
                    ?: return@MovePiece this

            val nextBoard = mutableBoard.copyOf().apply { perform(effect) }

            copy(
                previous = this to action,
                nextPlayer = nextPlayer.other(),
                mutableBoard = nextBoard,
            )
          }
        }
  }

  override fun actions(position: Position) =
      mutableBoard.actions(position, nextPlayer, history).map { it.first }
}
