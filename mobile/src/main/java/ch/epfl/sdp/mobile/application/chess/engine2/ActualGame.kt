package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine2.core.MutableBoard
import ch.epfl.sdp.mobile.application.chess.engine2.core.perform
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

data class ActualGame(
    override val previous: Pair<ActualGame, Action>?,
    private val mutableBoard: MutableBoard,
    private val nextPlayer: Color,
) : Game {

  override val board: Board<Piece<Color>> = mutableBoard.toBoard()

  override val nextStep: NextStep

  init {
    val hasActions = mutableBoard.hasAnyMoveAvailable(nextPlayer)
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
                mutableBoard.computeActions(action.from, nextPlayer).firstOrNull { (it, _) ->
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

  @OptIn(ExperimentalTime::class)
  override fun actions(position: Position): Sequence<Action> {
    val (actions, time) = measureTimedValue { mutableBoard.computeActions(position, nextPlayer) }
    println("Took $time to compute the available actions.")
    return actions.map { it.first }
  }
}
