package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.ui.game.MovesInfoState
import ch.epfl.sdp.mobile.ui.game.MovesInfoState.Move

class ActualMovesInfoState(private val delegate: GameChessBoardState) : MovesInfoState {

  override val moves: List<Move>
    get() = delegate.game.toAlgebraicNotation().map(::Move)
}
