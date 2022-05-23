package ch.epfl.sdp.mobile.ui.game.ar

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/**
 * A interface which represent tht state of a [ArChessBoardScreen]
 *
 * @param Piece the type of the [ChessBoardState.Piece].
 */
@Stable
interface ArGameScreenState<Piece : ChessBoardState.Piece> {

  // A map of pieces and their position that represent the current state of the game
  val pieces: Map<ChessBoardState.Position, Piece>

}
