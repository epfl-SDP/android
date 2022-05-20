package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine2.core.*

class MutableBoardScope(initial: MutableBoard) : BoardScope, EffectScope {

  // TODO : Optimize this by keeping an edit log instead of copying the board.
  private val boards = mutableListOf(initial)

  val current: MutableBoard
    get() = boards.last()

  override fun get(position: Position): Piece = current[position]

  fun save() {
    boards += current.copyOf()
  }

  override fun insert(position: Position, piece: Piece) {
    current[position] = piece
  }

  override fun remove(from: Position): Piece {
    val piece = current[from]
    current.remove(from)
    return piece
  }

  fun restore() {
    boards.removeLast()
  }

  inline fun <R> withSave(block: MutableBoardScope.(MutableBoard) -> R): R {
    try {
      save()
      return block(current)
    } finally {
      restore()
    }
  }
}
