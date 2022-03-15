package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.Move

data class ChessMove(override val number: Int, override val name: String) : Move

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

class FakeChessBoardState : GameScreenState<PieceIdentifier> {
  private var game by mutableStateOf(emptyGame())

  override val pieces: Map<ChessBoardState.Position, ChessBoardState.Piece<PieceIdentifier>>
    get() =
        Position.all()
            .map { game.board[it]?.let { p -> it to p } }
            .filterNotNull()
            .map { (a, b) -> a.toPosition() to b.toPiece() }
            .toMap()

  override val dragEnabled: Boolean
    get() = true // TODO: Change me!

  override fun onDropPiece(
      piece: ChessBoardState.Piece<PieceIdentifier>,
      startPosition: ChessBoardState.Position,
      endPosition: ChessBoardState.Position
  ) {
    val step = game.nextStep as NextStep.MovePiece
    game =
        step.move(
            Position(startPosition.x, startPosition.y),
            Delta(endPosition.x - startPosition.x, endPosition.y - startPosition.y),
        )
  }

  private val piecePool: List<String> = listOf("K", "Q", "N", "B", "R", "")
  private val columnPool: List<Char> = ('a'..'h').toList()
  private val rowPool: List<Char> = ('1'..'8').toList()

  override val moves: List<Move> =
      (1 until 20).map {
        val piece = piecePool[kotlin.random.Random.nextInt(0, piecePool.size)]
        val column = columnPool[kotlin.random.Random.nextInt(0, columnPool.size)]
        val row = rowPool[kotlin.random.Random.nextInt(0, rowPool.size)]
        val randomString = piece + column + row.toString()

        ChessMove(number = it, name = randomString)
      }
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
