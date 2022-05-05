package ch.epfl.sdp.mobile.state

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreenState
import kotlinx.coroutines.CoroutineScope

@Composable
fun StatefulPuzzleSelectionScreen(
  user: AuthenticatedUser,
  onPuzzleItemClick: (puzzle: PuzzleInfo) -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(),
) {
  val chess = LocalChessFacade.current
  val context = LocalContext.current
  val onGameItemClickAction = rememberUpdatedState(onPuzzleItemClick)
  val scope = rememberCoroutineScope()
  val state =
      remember(user, chess, scope) {
        SnapshotPuzzleSelectionScreen(
            onPuzzleClickAction = onGameItemClickAction,
            user = user,
            facade = chess,
            scope = scope,
            context = context,
        )
      }
  PuzzleSelectionScreen(
      state = state,
      modifier = modifier,
      contentPadding = contentPadding,
  )
}

private class SnapshotPuzzleSelectionScreen(
  onPuzzleClickAction: State<(puzzle: PuzzleInfo) -> Unit>,
  private val user: AuthenticatedUser,
  private val facade: ChessFacade,
  private val scope: CoroutineScope,
  private val context: Context,
) : PuzzleSelectionScreenState {

  val onPuzzleClickAction by onPuzzleClickAction

  override fun onPuzzleItemClick(puzzle: PuzzleInfo) {
    onPuzzleClickAction(puzzle)
  }

  override val puzzles =
      facade.unsolvedPuzzles(user, context).map { it.toPuzzleInfo() }.sortedBy { it.elo }
}

data class PuzzleItemAdapter(
    override val uid: String,
    override val playerColor: ChessBoardState.Color,
    override val elo: Int,
    override val icon: @Composable (() -> Unit)
) : PuzzleInfo

private fun Puzzle.toPuzzleInfo(): PuzzleInfo {
  val playerColor =
      when (this.boardSnapshot.playing) {
        Color.White -> ChessBoardState.Color.White
        Color.Black -> ChessBoardState.Color.Black
      }

  val board = this.boardSnapshot.board
  val firstMove = this.puzzleMoves.firstOrNull()?.from

  val firstPiece = firstMove?.let { board[firstMove] }

  val icon: @Composable () -> Unit = {
    when (firstPiece?.color) {
      Color.Black -> {
        when (firstPiece.rank) {
          Rank.King -> Icon(ChessIcons.BlackKing, contentDescription = null)
          Rank.Queen -> Icon(ChessIcons.BlackQueen, contentDescription = null)
          Rank.Rook -> Icon(ChessIcons.BlackRook, contentDescription = null)
          Rank.Bishop -> Icon(ChessIcons.BlackBishop, contentDescription = null)
          Rank.Knight -> Icon(ChessIcons.BlackKnight, contentDescription = null)
          Rank.Pawn -> Icon(ChessIcons.BlackPawn, contentDescription = null)
        }
      }
      Color.White -> {
        when (firstPiece.rank) {
          Rank.King -> Icon(ChessIcons.WhiteKing, contentDescription = null)
          Rank.Queen -> Icon(ChessIcons.WhiteQueen, contentDescription = null)
          Rank.Rook -> Icon(ChessIcons.WhiteRook, contentDescription = null)
          Rank.Bishop -> Icon(ChessIcons.WhiteBishop, contentDescription = null)
          Rank.Knight -> Icon(ChessIcons.WhiteKnight, contentDescription = null)
          Rank.Pawn -> Icon(ChessIcons.WhitePawn, contentDescription = null)
        }
      }
      else -> Icon(PawniesIcons.SectionPuzzles, contentDescription = null)
    }
  }

  return PuzzleItemAdapter(
      uid = this.uid,
      playerColor = playerColor,
      elo = this.elo,
      icon = icon,
  )
}
