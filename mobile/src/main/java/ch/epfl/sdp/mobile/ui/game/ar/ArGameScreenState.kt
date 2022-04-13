package ch.epfl.sdp.mobile.ui.game.ar

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

@Stable
interface ArGameScreenState<Piece : ChessBoardState.Piece> : ChessBoardState<Piece> {
  var chessScene: ChessScene<Piece>?
}
