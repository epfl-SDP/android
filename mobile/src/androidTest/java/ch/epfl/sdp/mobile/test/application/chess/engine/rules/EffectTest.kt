package ch.epfl.sdp.mobile.test.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect.Factory.combine
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect.Factory.move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect.Factory.remove
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect.Factory.replace
import com.google.common.truth.Truth.assertThat
import org.junit.Test

private typealias UnitPiece = Piece<Unit>

class EffectTest {

  private val uniquePieceIdentifier = PieceIdentifier(0)

  private val pawn: UnitPiece = Piece(Unit, Rank.Pawn, uniquePieceIdentifier)
  private val bishop: UnitPiece = Piece(Unit, Rank.Bishop, uniquePieceIdentifier)

  @Test
  fun remove_actuallyRemovesPiece() {
    val position = Position(0, 0)
    val board = buildBoard<UnitPiece> { set(position, pawn) }
    val effect = remove<UnitPiece>(position)
    assertThat(effect.perform(board)[position]).isNull()
  }

  @Test
  fun replace_placesNewPiece() {
    val position = Position(0, 0)
    val board = emptyBoard<UnitPiece>()
    val effect = replace(position, pawn)
    assertThat(effect.perform(board)[position]).isEqualTo(pawn)
  }

  @Test
  fun replace_overridesExistingPiece() {
    val position = Position(0, 0)
    val board = buildBoard<UnitPiece> { set(position, bishop) }
    val effect = replace(position, pawn)
    assertThat(effect.perform(board)[position]).isEqualTo(pawn)
  }

  @Test
  fun combine_identityOnBoard() {
    val board = emptyBoard<UnitPiece>()
    val effect = combine<UnitPiece>()
    assertThat(effect.perform(board)).isEqualTo(board)
  }

  @Test
  fun combine_appliesMultipleEffects() {
    val first = Position(0, 0)
    val second = Position(1, 1)
    val board =
        buildBoard<UnitPiece> {
          set(first, bishop)
          set(second, pawn)
        }
    val effect = combine<UnitPiece>(remove(first), remove(second))
    assertThat(effect.perform(board)).isEqualTo(emptyBoard<UnitPiece>())
  }

  @Test
  fun move_fromEmptyPosition_makesTargetEmpty() {
    val from = Position(0, 1)
    val to = Position(0, 0)
    val board = buildBoard<UnitPiece> { set(to, bishop) }
    val effect = move<UnitPiece>(from, to)
    assertThat(effect.perform(board)[to]).isEqualTo(null)
  }

  @Test
  fun move_outOfBounds_makesOriginEmpty() {
    val from = Position(0, 0)
    val to = Position(-1, -1)
    val board = buildBoard<UnitPiece> { set(from, pawn) }
    val effect = move<UnitPiece>(from, to)
    assertThat(effect.perform(board)[from]).isEqualTo(null)
  }

  @Test
  fun move_removesStartPieceAndReplacesEndPiece() {
    val from = Position(0, 1)
    val to = Position(0, 0)
    val board =
        buildBoard<UnitPiece> {
          set(from, pawn)
          set(to, bishop)
        }
    val effect = move<UnitPiece>(from, to)
    assertThat(effect.perform(board)[to]).isEqualTo(pawn)
  }
}
