package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingArState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ar.ArGameScreenState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import kotlinx.coroutines.CoroutineScope

fun ActualArScreenState(
    scope: CoroutineScope,
    match: Match,
): ActualArScreenState {

  val scene = ChessScene<Piece>(scope)

  val arScreenState = DelegatingArState(match, scene, scope)

  return ActualArScreenState(arScreenState)
}

class ActualArScreenState
constructor(
    arGameScreenState: ArGameScreenState<Piece>,
) : ArGameScreenState<Piece> by arGameScreenState {}
