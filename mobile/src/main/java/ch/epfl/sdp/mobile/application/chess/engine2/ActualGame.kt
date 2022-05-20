package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine2.core.MutableBoard

class ActualGame(
    previous: Pair<ActualGame, Action>?,
    private val mutableBoard: MutableBoard,
    private val nextPlayer: Color,
) : Game {

  override val previous: Pair<Game, Action>? = previous

  override val board: Board<Piece<Color>> = mutableBoard.toBoard()

  override val nextStep: NextStep = NextStep.MovePiece(nextPlayer, false) { action -> this }

  override fun actions(position: Position): Sequence<Action> {
    return mutableBoard.computeActions(position, nextPlayer)
  }
}
