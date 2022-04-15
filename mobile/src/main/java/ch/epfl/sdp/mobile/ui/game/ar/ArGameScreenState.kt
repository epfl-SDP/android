package ch.epfl.sdp.mobile.ui.game.ar

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/**
 * A state that indicate the content of a [ArChessBoard] composable.
 *
 * @param Piece the type of the pieces of the underlying [ChessBoardState].
 */
@Stable
interface ArGameScreenState<Piece : ChessBoardState.Piece> : ChessBoardState<Piece> {

  /** The scene that will be display in AR */
  val chessScene: ChessScene<Piece>

  /** Scale the whole scene with the given [value] */
  fun scale(value: Float)
}
