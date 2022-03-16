package ch.epfl.sdp.mobile.test.state

import ch.epfl.sdp.mobile.state.FakeChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StatefulGameTest {
  @Test
  fun movingAPawn_IsSuccessful() {
    val game = FakeChessBoardState()
    val initPos = Position(4, 1)
    val finalPos = Position(4, 2)

    val piece = requireNotNull(game.pieces[initPos])
    assertThat(piece.rank).isEqualTo(Pawn)
    assertThat(piece.color).isEqualTo(Black)

    game.onDropPiece(piece, finalPos)

    val newPiece = requireNotNull(game.pieces[finalPos])

    assertThat(newPiece.rank).isEqualTo(Pawn)
    assertThat(newPiece.color).isEqualTo(Black)
  }

  @Test
  fun movingAPawnThatNoLongerExists_DoesNothing() {
    val game = FakeChessBoardState()

    val initAttackerPos = Position(3, 0)
    val initVictimPos = Position(4, 1)
    val finalVictimPos = Position(4, 2)

    val victim = requireNotNull(game.pieces[initVictimPos])
    val attacker = requireNotNull(game.pieces[initAttackerPos])

    assertThat(victim.rank).isEqualTo(Pawn)
    assertThat(victim.color).isEqualTo(Black)

    assertThat(attacker.rank).isEqualTo(Queen)
    assertThat(attacker.color).isEqualTo(Black)

    // Eat the pawn with the queen
    game.onDropPiece(attacker, initVictimPos)

    // Try to move the pawn
    game.onDropPiece(victim, finalVictimPos)

    val pieceThatShouldNotExist = game.pieces[finalVictimPos]

    assertThat(pieceThatShouldNotExist).isNull()
  }

  @Test
  fun fixedMoveList_returnsExpectedMoves() {
    val game = FakeChessBoardState()
    val expected = listOf(1 to "f3", 2 to "e5", 3 to "g4", 4 to "Qh4#")

    assertThat(game.moves.map { it.number to it.name }).containsExactlyElementsIn(expected)
  }
}
