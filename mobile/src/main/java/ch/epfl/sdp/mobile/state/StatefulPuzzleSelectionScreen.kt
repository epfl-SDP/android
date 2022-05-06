package ch.epfl.sdp.mobile.state

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
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
) : PuzzleSelectionScreenState<PuzzleInfoAdapter> {

  val onPuzzleClickAction by onPuzzleClickAction

  override fun onPuzzleItemClick(puzzle: PuzzleInfoAdapter) {
    onPuzzleClickAction(puzzle)
  }

  override val puzzles =
      facade.unsolvedPuzzles(user).map { it.toPuzzleInfoAdapter() }.sortedBy { it.elo }
}

data class PuzzleInfoAdapter(
    override val uid: String,
    override val playerColor: ChessBoardState.Color,
    override val elo: Int,
    override val icon: @Composable (() -> Unit)
) : PuzzleInfo

private fun Puzzle.toPuzzleInfoAdapter(): PuzzleInfoAdapter {
  val playerColor =
      when (this.boardSnapshot.playing) {
        Color.White -> ChessBoardState.Color.White
        Color.Black -> ChessBoardState.Color.Black
      }

  val board = this.boardSnapshot.board
  val firstMove = this.puzzleMoves.firstOrNull()?.from

  val firstPiece = firstMove?.let { board[firstMove] }

  val icon: @Composable () -> Unit = {
    val desc = LocalLocalizedStrings.current.boardPieceContentDescription
    val color = firstPiece?.color.toString()
    val rank = firstPiece?.rank.toString()
    when (firstPiece?.color) {
      Color.Black -> {
        when (firstPiece.rank) {
          Rank.King -> Icon(ChessIcons.BlackKing, contentDescription = desc(color, rank))
          Rank.Queen -> Icon(ChessIcons.BlackQueen, contentDescription = desc(color, rank))
          Rank.Rook -> Icon(ChessIcons.BlackRook, contentDescription = desc(color, rank))
          Rank.Bishop -> Icon(ChessIcons.BlackBishop, contentDescription = desc(color, rank))
          Rank.Knight -> Icon(ChessIcons.BlackKnight, contentDescription = desc(color, rank))
          Rank.Pawn -> Icon(ChessIcons.BlackPawn, contentDescription = desc(color, rank))
        }
      }
      Color.White -> {
        when (firstPiece.rank) {
          Rank.King -> Icon(ChessIcons.WhiteKing, contentDescription = desc(color, rank))
          Rank.Queen -> Icon(ChessIcons.WhiteQueen, contentDescription = desc(color, rank))
          Rank.Rook -> Icon(ChessIcons.WhiteRook, contentDescription = desc(color, rank))
          Rank.Bishop -> Icon(ChessIcons.WhiteBishop, contentDescription = desc(color, rank))
          Rank.Knight -> Icon(ChessIcons.WhiteKnight, contentDescription = desc(color, rank))
          Rank.Pawn -> Icon(ChessIcons.WhitePawn, contentDescription = desc(color, rank))
        }
      }
      else -> Icon(PawniesIcons.SectionPuzzles, contentDescription = null)
    }
  }

  return PuzzleInfoAdapter(
      uid = this.uid,
      playerColor = playerColor,
      elo = this.elo,
      icon = icon,
  )
}
