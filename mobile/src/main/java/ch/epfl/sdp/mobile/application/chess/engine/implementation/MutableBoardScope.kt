package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.BoardScope
import ch.epfl.sdp.mobile.application.chess.engine.rules.EffectScope

/**
 * An implementation o [BoardScope] which uses a stack of [MutableBoard] internally to perform
 * incremental changes.
 *
 * @param initial the initial [MutableBoard].
 */
class MutableBoardScope(initial: MutableBoard) : BoardScope, EffectScope {

  /** The current state of the board stack. */
  private val boards = mutableListOf(initial)

  /** The current [MutableBoard], on which calls to [insert] and [remove] will be applied. */
  val current: MutableBoard
    get() = boards.last()

  override fun get(position: Position): MutableBoardPiece = current[position]

  /** Saves the current [MutableBoard], and pushes it onto the stack. */
  fun save() {
    // This could be optimized this by keeping an edit log instead of copying the board.
    boards += current.copyOf()
  }

  /** Restores the previous [MutableBoard] by popping the top of the stack. */
  fun restore() {
    boards.removeLast()
  }

  /**
   * Executes the [block] between some [save] and [restore] calls for the [MutableBoardScope].
   *
   * @param R the type of the return value.
   * @param block the block around which the [MutableBoardScope] is saved and restored.
   * @return the value returned by the block.
   */
  inline fun <R> withSave(block: MutableBoardScope.(MutableBoard) -> R): R {
    try {
      save()
      return block(current)
    } finally {
      restore()
    }
  }

  override fun insert(position: Position, piece: MutableBoardPiece) {
    current[position] = piece
  }

  override fun remove(from: Position): MutableBoardPiece {
    val piece = current[from]
    current.remove(from)
    return piece
  }
}
