package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The [StatefulPuzzleGameScreen] to be used for the Navigation
 *
 * @param user the currently logged-in user.
 * @param puzzleId the identifier for the puzzle.
 * @param modifier the [Modifier] for the composable.
 * @param paddingValues the [PaddingValues] for this composable.
 */
@Composable
fun StatefulPuzzleGameScreen(
    user: AuthenticatedUser,
    puzzleId: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) {
  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()

  val puzzle = facade.puzzle(uid = puzzleId) ?: Puzzle()

  val puzzleGameScreenState =
      remember(user, puzzle, scope) {
        SnapshotPuzzleBoardState(
            puzzle = puzzle,
            user = user,
            facade = facade,
            scope = scope,
        )
      }

  PuzzleGameScreen(
      state = puzzleGameScreenState,
      modifier = modifier,
      contentPadding = paddingValues,
  )
}

class SnapshotPuzzleBoardState(
    override val puzzle: Puzzle,
    private val user: AuthenticatedUser,
    private val facade: ChessFacade,
    private val scope: CoroutineScope,
) : PuzzleGameScreenState {
  override fun solve() {
    scope.launch { facade.solvePuzzle(puzzle, user) }
  }
}
