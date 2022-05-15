package ch.epfl.sdp.mobile.state.game.delegating

import ch.epfl.sdp.mobile.state.game.AbstractMovableChessBoardState
import ch.epfl.sdp.mobile.state.game.core.MutableGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ar.ArGameScreenState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DelegatingArState(
    private val delegate: MutableGameDelegate,
    private val promotion: DelegatingPromotionState,
    override val chessScene: ChessScene<Piece>,
    private val scope: CoroutineScope
) : ArGameScreenState<Piece>, AbstractMovableChessBoardState(delegate) {

  override fun onLoad(startingBoard: Map<ChessBoardState.Position, Piece>, boardScale: Float) {
    scope.launch {
      chessScene.loadStartingBoard(startingBoard)
      chessScene.scale(boardScale)
    }
  }

  override fun move(from: ChessBoardState.Position, to: ChessBoardState.Position) {
    TODO("Not yet implemented")
  }
}
