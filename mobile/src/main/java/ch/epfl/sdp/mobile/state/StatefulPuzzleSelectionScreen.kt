package ch.epfl.sdp.mobile.state

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.puzzles.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleItem
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreenState
import kotlinx.coroutines.CoroutineScope

@Composable
fun StatefulPuzzleSelectionScreen(
    user: AuthenticatedUser,
    onPuzzleItemClick: (puzzle: PuzzleItem) -> Unit,
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
    onPuzzleClickAction: State<(puzzle: PuzzleItem) -> Unit>,
    private val user: AuthenticatedUser,
    private val facade: ChessFacade,
    private val scope: CoroutineScope,
    private val context: Context,
) : PuzzleSelectionScreenState {

  val onPuzzleClickAction by onPuzzleClickAction

  override fun onPuzzleItemClick(puzzle: PuzzleItem) {
    onPuzzleClickAction(puzzle)
  }

  override val puzzles = facade.unsolvedPuzzles(user, context).map { it.toPuzzleItem() }.sortedBy { it.elo }
}

data class PuzzleItemAdapter(
    override val uid: String,
    override val playerColor: ChessBoardState.Color,
    override val elo: Int,
) : PuzzleItem

private fun Puzzle.toPuzzleItem(): PuzzleItem {
  val color =
      when (this.boardSnapshot.playing) {
        Color.White -> ChessBoardState.Color.White
        Color.Black -> ChessBoardState.Color.Black
      }

  return PuzzleItemAdapter(
      uid = this.uid,
      playerColor = color,
      elo = this.elo,
  )
}
