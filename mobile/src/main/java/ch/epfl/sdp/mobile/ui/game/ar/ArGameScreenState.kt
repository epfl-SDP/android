package ch.epfl.sdp.mobile.ui.game.ar

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.MovableChessBoardState

@Stable
interface ArGameScreenState<Piece : ChessBoardState.Piece> : MovableChessBoardState<Piece> {

  val chessScene: ChessScene<Piece>

  fun onLoad(startingBoard: Map<ChessBoardState.Position, Piece>, boardScale: Float)
}
