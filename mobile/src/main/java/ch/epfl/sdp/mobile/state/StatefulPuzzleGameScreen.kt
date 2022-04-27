package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.state.SnapshotChessBoardState.SnapshotPiece
import ch.epfl.sdp.mobile.ui.puzzles.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreenState
import ch.epfl.sdp.mobile.ui.puzzles.SnapshotPuzzle
import kotlinx.coroutines.CoroutineScope

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

  val puzzleMoves = emptyList<Action>()
  val boardSnapshot =
      FenNotation.BoardSnapshot(
          board = emptyBoard(),
          playing = Color.White,
          castlingRights =
              FenNotation.CastlingRights(
                  kingSideWhite = true,
                  queenSideWhite = true,
                  kingSideBlack = true,
                  queenSideBlack = true,
              ),
          enPassant = null,
          halfMoveClock = 0,
          fullMoveClock = 0,
      )

  val puzzle =
      remember(puzzleId) {
        SnapshotPuzzle(
            puzzleId,
            boardSnapshot,
            puzzleMoves,
        )
      }

  val puzzleGameScreenState =
      remember(user, puzzle, scope) {
        SnapshotPuzzleBoardState(
            user = user,
            puzzle = puzzle,
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
    private val scope: CoroutineScope,
) : PuzzleGameScreenState<SnapshotPiece>
