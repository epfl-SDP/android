package ch.epfl.sdp.mobile.state.game.delegating

import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.ui.game.MovesInfoState
import ch.epfl.sdp.mobile.ui.game.MovesInfoState.Move

/**
 * An implementation of [MovesInfoState] which uses a [GameDelegate] to extract the moves
 * information.
 *
 * @param delegate the underlying [GameDelegate].
 */
class DelegatingMovesInfoState(private val delegate: GameDelegate) : MovesInfoState {

  override val moves: List<Move>
    get() = delegate.game.toAlgebraicNotation().map(::Move)
}
