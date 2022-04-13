package ch.epfl.sdp.mobile.state

import android.content.Context
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Piece as EnginePiece
import ch.epfl.sdp.mobile.state.SnapshotArChessBoardState.SnapshotArPiece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ar.ArChessBoard
import ch.epfl.sdp.mobile.ui.game.ar.ArGameScreenState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import com.gorisse.thomas.lifecycle.lifecycleScope

@Composable
fun StatefulArScreen(
    modifier: Modifier = Modifier,
) {

  val context = LocalContext.current
  val view = LocalView.current

  val chessScene by remember { mutableStateOf<ChessScene<SnapshotArPiece>?>(null) }

  val gameScreenState =
      remember(context, view, chessScene) { SnapshotArChessBoardState(context, view, chessScene) }

  ArChessBoard(gameScreenState, modifier)
}

/**
 * An implementation of [ChessBoardState] that starts with default chess position that can be
 * display in AR
 */
class SnapshotArChessBoardState(
    context: Context,
    view: View,
    override var chessScene: ChessScene<SnapshotArPiece>?
) : ArGameScreenState<SnapshotArPiece> {

  private var game by mutableStateOf(Game.create())

  init {
    chessScene = ChessScene(context, view.lifecycleScope, pieces)
  }
  /** An Implementation of [ChessBoardState.Piece] used to display */
  data class SnapshotArPiece(
      val id: PieceIdentifier,
      override val rank: ChessBoardState.Rank,
      override val color: ChessBoardState.Color
  ) : ChessBoardState.Piece

  // TODO : Duplicate from [SnapshotChessBoardState]
  override val pieces: Map<ChessBoardState.Position, SnapshotArPiece>
    get() =
        game.board
            .asSequence()
            .map { (pos, piece) -> pos.toPosition() to piece.toArPiece() }
            .toMap()
  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = game.nextStep
      if (nextStep !is NextStep.MovePiece || !nextStep.inCheck) return null
      return game.board
          .first { (_, piece) -> piece.color == nextStep.turn && piece.rank == Rank.King }
          .first
          .toPosition()
    }
}

// TODO : Duplicate from [SnapshotChessBoardState]
fun EnginePiece<Color>.toArPiece(): SnapshotArPiece {
  val rank =
      when (this.rank) {
        Rank.King -> ChessBoardState.Rank.King
        Rank.Queen -> ChessBoardState.Rank.Queen
        Rank.Rook -> ChessBoardState.Rank.Rook
        Rank.Bishop -> ChessBoardState.Rank.Bishop
        Rank.Knight -> ChessBoardState.Rank.Knight
        Rank.Pawn -> ChessBoardState.Rank.Pawn
      }

  val color =
      when (this.color) {
        Color.Black -> ChessBoardState.Color.Black
        Color.White -> ChessBoardState.Color.White
      }

  return SnapshotArPiece(id = this.id, rank = rank, color = color)
}
