package ch.epfl.sdp.mobile.ui.game.ar

import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position as EnginePosition
import io.github.sceneview.math.Position as ArPosition

/**
 * This class contains information about the AR chess board model
 *
 * @param boardBorderSize offset between the board boarder and the 1st cell
 * @param boardHeight The height of the model
 * @param boardHalfSize The half size of the whole chessboard
 */
data class ArBoard(
    private val boardBorderSize: Float,
    private val boardHeight: Float,
    private val boardHalfSize: Float,
) {

  private val cellSize = (boardHalfSize - boardBorderSize) / 4
  private val cellCenter = cellSize / 2

  /** For the given [EnginePosition] and transform it into AR board [ArPosition] */
  fun toArPosition(position: EnginePosition): ArPosition {

    fun transform(value: Int): Float {
      return -boardHalfSize + boardBorderSize + value * cellSize + cellCenter
    }
    return ArPosition(x = transform(position.x), y = boardHeight, z = transform(position.y))
  }
}
