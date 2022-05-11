package ch.epfl.sdp.mobile.state.game.core

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.Puzzle
import ch.epfl.sdp.mobile.application.chess.baseGame
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPuzzleInfoState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * An implementation of [MutableGameDelegate] which writes its updates to a [Match].
 *
 * @param user the [State] of the current [AuthenticatedUser].
 * @param puzzle the underlying [Puzzle].
 * @param delegate the [DelegatingPuzzleInfoState] that provides the puzzle's state info.
 * @param scope the [CoroutineScope] used to read and write the match updates.
 */
class PuzzleGameDelegate(
    private val user: State<AuthenticatedUser>,
    private val puzzle: Puzzle,
    private val delegate: DelegatingPuzzleInfoState,
    private val scope: CoroutineScope,
) : MutableGameDelegate, PuzzleInfoState by delegate {

  /** The underlying snapshot-aware [Game]. */
  private var backing by mutableStateOf(puzzle.baseGame())

  override var game: Game
    get() = backing
    set(value) {
      backing = value // Local updates
    }

  override fun tryPerformAction(action: Action): Boolean {
    val step = game.nextStep as? NextStep.MovePiece ?: return false
    if (action !in game.actions(action.from)) return false

    // Puzzle shenanigans
    playPuzzleAction(step, action)
    return true
  }

  /**
   * Plays a move for the player. If it's the expected one, play the bot's move if there is one. If
   * it's not the expected one, reset the puzzle. Bot actions and puzzle reset happen after "delay"
   * milliseconds
   *
   * @param step The next [Game] step.
   * @param action The action played by the user.
   * @param delay The delay to way before the bot plays or the puzzle is reset.
   */
  private fun playPuzzleAction(step: NextStep.MovePiece, action: Action, delay: Long = 1000) {
    val expected = puzzle.puzzleMoves[currentMoveNumber]

    game = step.move(action)
    currentMoveNumber++

    if (action == expected) {
      scope.launch { attemptNextBotMove(delay) }
    } else {
      scope.launch { resetPuzzle(delay) }
    }
  }

  /** Marks the puzzle as failed, then resets the puzzle after delay milliseconds. */
  private suspend fun resetPuzzle(delay: Long) {
    puzzleState = PuzzleInfoState.PuzzleState.Failed
    delay(delay)
    game = puzzle.baseGame()
    currentMoveNumber = 1
    puzzleState = PuzzleInfoState.PuzzleState.Solving
  }

  /**
   * Attempts to play the next bot move after delay milliseconds. If there is none, mark the puzzle
   * as solved.
   */
  private suspend fun attemptNextBotMove(delay: Long) {
    if (currentMoveNumber < puzzle.puzzleMoves.size) {
      delay(delay)
      val action = puzzle.puzzleMoves[currentMoveNumber]
      val step = game.nextStep as? NextStep.MovePiece ?: return

      game = step.move(action)
      currentMoveNumber++
    } else {
      puzzleState = PuzzleInfoState.PuzzleState.Solved
      // TODO: Only mark puzzles as solved once the "Solved Puzzles" screen is implemented
      // scope.launch { user.value.solvePuzzle(puzzle) }
    }
  }
}
