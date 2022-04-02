package ch.epfl.sdp.mobile.ui.ar

import io.github.sceneview.math.Position

/**
 * This class contains information about the AR chess board model
 *
 * @param boardBorderSize offset between the board boarder and the 1st cell
 * @param boardHeight The height of the model
 * @param boardHalfSize The half size of the whole chessboard
 */
data class ArBoard(
    val boardBorderSize: Float,
    val boardHeight: Float,
    val boardHalfSize: Float,
) {

  private val cellSize = (boardHalfSize - boardBorderSize) / 4
  private val cellCenter = cellSize / 2

  /** For the given [ChessBoardState.Position] and transform it into AR board [Position] */
  fun toArPosition(position: ch.epfl.sdp.mobile.application.chess.engine.Position): Position {

    fun transform(value: Int): Float {
      return -boardHalfSize + boardBorderSize + value * cellSize + cellCenter
    }
    return Position(x = transform(position.x), y = boardHeight, z = transform(position.y))
  }
}
