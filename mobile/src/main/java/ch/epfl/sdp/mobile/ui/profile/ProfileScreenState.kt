package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * The view-model of the profile of the screen.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 */
@Stable
interface ProfileScreenState<C : ChessMatch, P : PuzzleInfo> : Person {

  /** Number of past games. */
  val pastGamesCount: Int

  /** List of chess matches. */
  val matches: List<C>

  /** Number of solved puzzles. */
  val solvedPuzzlesCount: Int

  /** List of solved puzzles. */
  val puzzles: List<P>

  /**
   * Callback function to open a match.
   *
   * @param match the [ChessMatch] to open.
   */
  fun onMatchClick(match: C)

  /**
   * Callback function to open a puzzle.
   *
   * @param puzzle the [PuzzleInfo] to open.
   */
  fun onPuzzleClick(puzzle: P)
}
