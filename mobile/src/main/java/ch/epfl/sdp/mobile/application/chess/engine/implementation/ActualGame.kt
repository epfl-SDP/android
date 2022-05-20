package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.perform
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
data class ActualGame(
    override val previous: Pair<ActualGame, Action>?,
    private val mutableBoard: MutableBoard,
    private val nextPlayer: Color,
) : Game {

  override val board: Board<Piece<Color>> = mutableBoard.toBoard()

  private val history = sequence {
    var current: ActualGame? = this@ActualGame
    while (current != null) {
      yield(current.board)
      current = current.previous?.first
    }
  }

  override val nextStep: NextStep

  init {
    val hasActions = mutableBoard.hasAnyMoveAvailable(nextPlayer, history)
    val inCheck = mutableBoard.inCheck(nextPlayer)
    val (nextStep, time) =
        measureTimedValue {
          if (!hasActions) {
            if (inCheck) {
              NextStep.Checkmate(nextPlayer.other())
            } else {
              NextStep.Stalemate
            }
          } else {
            NextStep.MovePiece(nextPlayer, inCheck) { action ->
              val (_, effect) =
                  mutableBoard.computeActions(action.from, nextPlayer, history).firstOrNull {
                      (it, _) ->
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
    this.nextStep = nextStep
    println("Took $time to compute the nextStep")
  }

  @OptIn(ExperimentalTime::class)
  override fun actions(position: Position): Sequence<Action> {
    val (actions, time) =
        measureTimedValue { mutableBoard.computeActions(position, nextPlayer, history) }
    println("Took $time to compute the available actions.")
    return actions.map { it.first }
  }
}
