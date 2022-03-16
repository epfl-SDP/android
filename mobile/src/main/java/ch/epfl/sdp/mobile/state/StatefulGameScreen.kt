package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.Move

/**
 * Represents a [ChessBoardState]'s Move
 * @property number The move's number in the game's history
 * @property name The move's name in chess notation
 */
data class ChessMove(override val number: Int, override val name: String) : Move

/** Maps a game engine [Position] to a [ChessBoardState.Position] */
fun Position.toPosition(): ChessBoardState.Position {
  return ChessBoardState.Position(this.x, this.y)
}

fun Piece.toPiece(): ChessBoardState.Piece<PieceIdentifier> {
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

  return ChessBoardState.Piece(id = this.id, rank = rank, color = color)
}

/**
 * An implementation of [GameScreenState] that starts with default chess positions,
 * can move pieces and has a static move list
 */
class FakeChessBoardState : GameScreenState<PieceIdentifier> {
  private var game by mutableStateOf(emptyGame())

  override val pieces: Map<ChessBoardState.Position, ChessBoardState.Piece<PieceIdentifier>>
    get() =
        Position.all()
            .map { game.board[it]?.let { p -> it to p } }
            .filterNotNull()
            .map { (a, b) -> a.toPosition() to b.toPiece() }
            .toMap()

  override fun onDropPiece(
      piece: ChessBoardState.Piece<PieceIdentifier>,
      endPosition: ChessBoardState.Position
  ) {
    val startPosition = pieces.entries.firstOrNull { it.value == piece }?.key ?: return
    val step = game.nextStep as NextStep.MovePiece

    game =
        step.move(
            Position(startPosition.x, startPosition.y),
            Delta(endPosition.x - startPosition.x, endPosition.y - startPosition.y),
        )
  }

  override val moves: List<Move> =
      listOf(
          ChessMove(1, "f3"),
          ChessMove(2, "e5"),
          ChessMove(3, "g4"),
          ChessMove(4, "Qh4#"),
      )
}

@Composable
fun rememberGameScreenState(): GameScreenState<PieceIdentifier> {
  return remember { FakeChessBoardState() }
}

@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  GameScreen(rememberGameScreenState(), modifier)
}
