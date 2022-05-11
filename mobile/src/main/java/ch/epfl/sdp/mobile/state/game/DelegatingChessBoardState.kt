package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.state.game.core.MatchGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import kotlinx.coroutines.CoroutineScope

fun ActualChessBoardState(
    match: Match,
    scope: CoroutineScope,
): ActualChessBoardState {
  val delegate = MatchGameDelegate(match, scope)
  return ActualChessBoardState(delegate)
}

class ActualChessBoardState(
    delegate: GameDelegate,
) : ChessBoardState<Piece> by DelegatingChessBoardState(delegate)
