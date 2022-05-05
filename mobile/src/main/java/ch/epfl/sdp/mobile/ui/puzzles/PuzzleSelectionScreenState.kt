package ch.epfl.sdp.mobile.ui.puzzles

/** Interface that represents state of the PlayScreen */
interface PuzzleSelectionScreenState<P : PuzzleInfo> {

  /** Action to execute when clicked on match item in list */
  fun onPuzzleItemClick(puzzle: P)

  /** List of matches of current user */
  val puzzles: List<P>
}
