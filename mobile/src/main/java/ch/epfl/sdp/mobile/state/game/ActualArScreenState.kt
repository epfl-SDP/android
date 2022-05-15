package ch.epfl.sdp.mobile.state.game

import android.content.Context
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.game.core.MatchGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingArState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.*
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPromotionState
import ch.epfl.sdp.mobile.ui.game.ar.ArGameScreenState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import kotlinx.coroutines.CoroutineScope

fun ActualArScreenState(
    scope: CoroutineScope,
    match: Match,
): ActualArScreenState {

  val scene = ChessScene<Piece>(scope)

  val chessBoard = MatchGameDelegate(match, scope)

  val promotions = DelegatingPromotionState(chessBoard)

  val delegate = MatchGameDelegate(match, scope)

  val arScreenState = DelegatingArState(delegate, promotions, scene, scope)

  return ActualArScreenState(arScreenState)
}

class ActualArScreenState
constructor(
    arGameScreenState: ArGameScreenState<Piece>,
) : ArGameScreenState<Piece> by arGameScreenState {}
