package ch.epfl.sdp.mobile.application.chess

/**
 * A [BoardWithHistory] is a [Board] which provides access to historical information, such as the
 * previous moves which were performed. Multiple updates may be performed atomically on a [Board] to
 * obtain a new [BoardWithHistory].
 *
 * @param Piece the type of the pieces on the board.
 */
interface BoardWithHistory<Piece : Any> : Board<Piece> {

  /** The previous state of the [BoardWithHistory], if it exists. */
  val previousBoardWithHistory: BoardWithHistory<Piece>?
}

/**
 * Returns a [Sequence] of all the previous [Board] configurations that this [BoardWithHistory]
 * previously was in. The [Sequence] is provided in reverse order, starting with the current [Board]
 * and stopping when no [BoardWithHistory.previousBoardWithHistory] can be found.
 *
 * @param Piece the type of the pieces.
 */
fun <Piece : Any> BoardWithHistory<Piece>.asSequence(): Sequence<Board<Piece>> = sequence {
  var current: BoardWithHistory<Piece>? = this@asSequence
  while (current != null) {
    yield(current)
    current = current.previousBoardWithHistory
  }
}
