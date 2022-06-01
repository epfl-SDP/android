package ch.epfl.sdp.mobile.ui.puzzles

/**
 * Interface that represents state of the [PuzzleSelectionScreen].
 *
 * @param P The actual [PuzzleInfo] type.
 */
interface PuzzleSelectionScreenState<P : PuzzleInfo> {

  /**
   * Action to execute when clicking on puzzle item in list.
   *
   * @param puzzle The puzzle that is clicked.
   */
  fun onPuzzleItemClick(puzzle: P)

  /** List of puzzles of current user. */
  val puzzles: List<P>
}
