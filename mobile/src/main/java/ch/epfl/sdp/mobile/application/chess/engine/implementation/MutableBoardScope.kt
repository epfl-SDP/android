package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.BoardScope
import ch.epfl.sdp.mobile.application.chess.engine.rules.EffectScope

class MutableBoardScope(initial: MutableBoard) : BoardScope, EffectScope {

  // TODO : Optimize this by keeping an edit log instead of copying the board.
  private val boards = mutableListOf(initial)

  val current: MutableBoard
    get() = boards.last()

  override fun get(position: Position): MutableBoardPiece = current[position]

  fun save() {
    boards += current.copyOf()
  }

  override fun insert(position: Position, piece: MutableBoardPiece) {
    current[position] = piece
  }

  override fun remove(from: Position): MutableBoardPiece {
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
