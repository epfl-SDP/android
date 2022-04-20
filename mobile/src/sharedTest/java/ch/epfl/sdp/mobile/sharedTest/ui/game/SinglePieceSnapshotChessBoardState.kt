package ch.epfl.sdp.mobile.sharedTest.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.MovableChessBoardState

/**
 * An implementation of [MovableChessBoardState] which moves a single piece around the chessboard on
 * drag and drops.
 *
 * @param piece the [Piece] which should be moved.
 */
class SinglePieceSnapshotChessBoardState(
    private val piece: Piece =
        object : Piece {
          override val color = ChessBoardState.Color.White
          override val rank = ChessBoardState.Rank.Pawn
        },
) : MovableChessBoardState<Piece> {

  var position: ChessBoardState.Position by mutableStateOf(ChessBoardState.Position(0, 0))

  override val selectedPosition: ChessBoardState.Position? = null
  override val checkPosition: ChessBoardState.Position? = null

  override val pieces: Map<ChessBoardState.Position, Piece>
    get() = mapOf(position to piece)

  override val availableMoves: Set<ChessBoardState.Position>
    get() = emptySet()

  override fun onDropPiece(
      piece: Piece,
      endPosition: ChessBoardState.Position,
  ) {
    position = endPosition
  }

  override fun onPositionClick(position: ChessBoardState.Position) = Unit
}
