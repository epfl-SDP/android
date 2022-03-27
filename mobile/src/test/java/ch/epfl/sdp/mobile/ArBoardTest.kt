package ch.epfl.sdp.mobile

import ch.epfl.sdp.mobile.ui.ar.ArBoard
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import org.junit.Assert.*
import org.junit.Test

class ArBoardTest {

  private val board = ArBoard(2f, 1f, 10f)

  @Test
  fun `Ensure that the position is translated correctly`() {
    val p = ChessBoardState.Position(1, 1)
    val arPosition = board.toArPosition(p)

    assertEquals(-5f, arPosition.x)
    assertEquals(-5f, arPosition.z)
  }
}
