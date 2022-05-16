package ch.epfl.sdp.mobile.ui.game.ar

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

@Stable
interface ArGameScreenState<Piece : ChessBoardState.Piece> {

  val chessScene: ChessScene<Piece>

  val pieces: Map<ChessBoardState.Position, Piece>

  fun onLoad(boardScale: Float)

  fun update()
}
