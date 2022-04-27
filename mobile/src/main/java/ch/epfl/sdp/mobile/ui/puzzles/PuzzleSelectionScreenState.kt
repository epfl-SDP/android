package ch.epfl.sdp.mobile.ui.puzzles

/** Interface that represents state of the PlayScreen */
// TODO: Add Generic Puzzle Type ?
interface PuzzleSelectionScreenState {

  /** Action to execute when clicked on match item in list */
  fun onPuzzleClick(puzzle: Puzzle)

  /** List of matches of current user */
  val puzzles: List<PuzzleItem>
}