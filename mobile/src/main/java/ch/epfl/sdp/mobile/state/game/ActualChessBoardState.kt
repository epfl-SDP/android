package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.state.game.core.MatchGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import kotlinx.coroutines.CoroutineScope

/**
 * Builds an [ActualChessBoardState].
 *
 * @param match the [Match] for which the state is built.
 * @param scope the [CoroutineScope] with which the match will be loaded.
 */
fun ActualChessBoardState(
    match: Match,
    scope: CoroutineScope,
): ActualChessBoardState {
  val delegate = MatchGameDelegate(match, scope)
  return ActualChessBoardState(delegate)
}

/**
 * An implementation of [ChessBoardState] which delegates its state to a [GameDelegate].
 *
 * @param delegate the underlying [GameDelegate].
 */
class ActualChessBoardState(
    delegate: GameDelegate,
) : ChessBoardState<Piece> by DelegatingChessBoardState(delegate)
