package ch.epfl.sdp.mobile.test.application.chess

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.BoardWithHistory

/**
 * An implementation of [BoardWithHistory] which can be used to build some fake histories of games.
 *
 * @param Piece the type of the pieces.
 */
private data class FakeBoardWithHistory<Piece : Any>(
    val board: Board<Piece>,
    private val previous: FakeBoardWithHistory<Piece>?
) : BoardWithHistory<Piece>, Board<Piece> by board {
  override val previousBoardWithHistory: BoardWithHistory<Piece>? = previous
}

/**
 * Builds a [BoardWithHistory] using the provided sequence of [Board], which are applied in order.
 *
 * @param Piece the type of the pieces.
 * @param block the [SequenceScope] block in which the boards are created.
 * @return the [BoardWithHistory], where the first [Board] corresponds to the last emission.
 */
fun <Piece : Any> buildBoardWithHistory(
    block: suspend SequenceScope<Board<Piece>>.() -> Unit,
): BoardWithHistory<Piece> {
  val boards = sequence(block).toList()
  if (boards.isEmpty()) throw IllegalStateException("You must yield at least one board.")
  val head = boards.first()
  val tail = boards.drop(1)
  return tail.fold(FakeBoardWithHistory(head, null)) { history, board ->
    FakeBoardWithHistory(board, history)
  }
}
