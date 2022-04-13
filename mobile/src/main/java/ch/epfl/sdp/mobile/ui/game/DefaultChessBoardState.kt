package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.engine.*

abstract class DefaultChessBoardState<Piece : ChessBoardState.Piece> :
    ChessBoardState<Piece> {

  // TODO : Restrict access ?
  var game by mutableStateOf(Game.create())

  override val pieces: Map<ChessBoardState.Position, Piece>
    get() =
        game.board.asSequence().map { (pos, piece) -> pos.toPosition() to piece.toPiece() }.toMap()

  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = game.nextStep
      if (nextStep !is NextStep.MovePiece || !nextStep.inCheck) return null
      return game.board
          .first { (_, piece) -> piece.color == nextStep.turn && piece.rank == Rank.King }
          .first
          .toPosition()
    }

  /** Maps a game engine [Position] to a [ChessBoardState.Position] */
  private fun Position.toPosition(): ChessBoardState.Position {
    return ChessBoardState.Position(this.x, this.y)
  }
}
