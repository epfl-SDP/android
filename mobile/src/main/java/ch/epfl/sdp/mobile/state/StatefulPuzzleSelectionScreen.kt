package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toColor
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreenState

/**
 * The [StatefulPuzzleSelectionScreen] to be used for the Navigation.
 *
 * @param user the currently logged-in user.
 * @param onPuzzleItemClick An action triggered when clicking on an item in the puzzle list
 * @param modifier the [Modifier] for the composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun StatefulPuzzleSelectionScreen(
    user: AuthenticatedUser,
    onPuzzleItemClick: (puzzle: PuzzleInfo) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chess = LocalChessFacade.current
  val onGameItemClickAction = rememberUpdatedState(onPuzzleItemClick)
  val scope = rememberCoroutineScope()
  val state =
      remember(user, chess, scope) {
        SnapshotPuzzleSelectionScreen(
            onPuzzleClickAction = onGameItemClickAction,
            user = user,
            facade = chess,
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
    user: AuthenticatedUser,
    facade: ChessFacade,
) : PuzzleSelectionScreenState<PuzzleInfoAdapter> {

  val onPuzzleClickAction by onPuzzleClickAction

  override fun onPuzzleItemClick(puzzle: PuzzleInfoAdapter) {
    onPuzzleClickAction(puzzle)
  }

  override val puzzles =
      facade.unsolvedPuzzles(user).map { it.toPuzzleInfoAdapter() }.sortedBy { it.elo }
}

/**
 * Represents the basic info of a [Puzzle] to display it in a list.
 *
 * @property uid The [Puzzle]'s uid.
 * @property playerColor The [Color] of the player in the [Puzzle].
 * @property elo The elo/rank (difficulty) of the puzzle.
 * @property icon The icon [Composable] to display next to the [Puzzle] description.
 */
data class PuzzleInfoAdapter(
    override val uid: String,
    override val playerColor: ChessBoardState.Color,
    override val elo: Int,
    override val icon: @Composable () -> Unit
) : PuzzleInfo

/** Transforms a [Puzzle] to a corresponding [PuzzleInfoAdapter]. */
fun Puzzle.toPuzzleInfoAdapter(): PuzzleInfoAdapter {
  // Inverted colors to FEN since first UCI moves describes "computer" move
  val playerColor = this.boardSnapshot.playing.other().toColor()

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
      else -> Icon(PawniesIcons.SectionPuzzlesUnselected, contentDescription = null)
    }
  }

  return PuzzleInfoAdapter(
      uid = this.uid,
      playerColor = playerColor,
      elo = this.elo,
      icon = icon,
  )
}
