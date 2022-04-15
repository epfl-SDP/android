package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.state.BasicSnapshotBoardState.SnapshotPiece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A basic implementation of [ChessBoardState] that add the strict minimum feature need for the
 * snapshot using [SnapshotPiece]:
 *
 * - Create a [Game] and retrieve the current state
 * - Retrieve the [Piece] on the board
 * - Implement the computation of the [ChessBoardState.Rank.King] position if in check
 *
 * @param match the match to display.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 */
open class BasicSnapshotBoardState(
    private val match: Match,
    scope: CoroutineScope,
) : ChessBoardState<SnapshotPiece> {

  var game by mutableStateOf(Game.create())

  init {
    scope.launch { match.game.collect { game = it } }
  }

  override val pieces: Map<ChessBoardState.Position, SnapshotPiece>
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

  /**
   * An implementation of [ChessBoardState.Piece] which uses a [PieceIdentifier] to disambiguate
   * different pieces.
   *
   * @param id the unique [PieceIdentifier].
   * @param color the color for the piece.
   * @param rank the rank for the piece.
   */
  data class SnapshotPiece(
      val id: PieceIdentifier,
      override val color: ChessBoardState.Color,
      override val rank: ChessBoardState.Rank,
  ) : ChessBoardState.Piece
}

fun Piece<Color>.toPiece(): SnapshotPiece {
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

  return SnapshotPiece(id = this.id, rank = rank, color = color)
}

/** Maps a game engine [Position] to a [ChessBoardState.Position] */
fun Position.toPosition(): ChessBoardState.Position {
  return ChessBoardState.Position(this.x, this.y)
}
