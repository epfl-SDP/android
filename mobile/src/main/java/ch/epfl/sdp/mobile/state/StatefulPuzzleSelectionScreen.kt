package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.puzzles.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleItem
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleSelectionScreenState
import kotlinx.coroutines.CoroutineScope

@Composable
fun StatefulPuzzleSelectionScreen(
    user: AuthenticatedUser,
    onPuzzleItemClick: (puzzle: Puzzle) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chess = LocalChessFacade.current
  val onGameItemClickAction = rememberUpdatedState(onPuzzleItemClick)
  val scope = rememberCoroutineScope()
  val state =
      remember(
          user,
          chess,
          scope,
      ) {
        SnapshotPuzzleSelectionScreen(
            user = user,
            onPuzzleClickAction = onGameItemClickAction,
            scope = scope,
        )
      }
  PuzzleSelectionScreen(
      state = state,
      modifier = modifier,
      contentPadding = contentPadding,
  )
}

private class SnapshotPuzzleSelectionScreen(
    onPuzzleClickAction: State<(puzzle: Puzzle) -> Unit>,
    private val user: AuthenticatedUser,
    private val scope: CoroutineScope,
) : PuzzleSelectionScreenState {

  val onPuzzleClickAction by onPuzzleClickAction

  override fun onPuzzleClick(puzzle: Puzzle) {
    onPuzzleClickAction(puzzle)
  }

  override val puzzles =
      listOf(
          PuzzleItemAdapter(uid = "000001", playerColor = ChessBoardState.Color.White, elo = 1500),
          PuzzleItemAdapter(uid = "000002", playerColor = ChessBoardState.Color.White, elo = 2000),
          PuzzleItemAdapter(uid = "000003", playerColor = ChessBoardState.Color.White, elo = 2500),
      )
}

data class PuzzleItemAdapter(
    override val uid: String,
    override val playerColor: ChessBoardState.Color,
    override val elo: Int,
) : PuzzleItem
