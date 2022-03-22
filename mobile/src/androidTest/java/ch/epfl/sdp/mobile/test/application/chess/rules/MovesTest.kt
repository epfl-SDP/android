package ch.epfl.sdp.mobile.test.application.chess.rules

import ch.epfl.sdp.mobile.application.chess.Delta
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position
import ch.epfl.sdp.mobile.application.chess.Rank.Pawn
import ch.epfl.sdp.mobile.application.chess.implementation.PersistentPieceIdentifier
import ch.epfl.sdp.mobile.application.chess.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.rules.*
import ch.epfl.sdp.mobile.application.chess.rules.Role.Adversary
import ch.epfl.sdp.mobile.application.chess.rules.Role.Allied
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovesTest {

  private val adversaryPawn = Piece(Adversary, Pawn, PersistentPieceIdentifier(0))
  private val alliedPawn = Piece(Allied, Pawn, PersistentPieceIdentifier(0))

  @Test
  fun delta_outOfBounds_hasNoActions() {
    val board = emptyBoard<Piece<Role>>()
    val moves = board.delta(Position(0, 0), x = -1, y = -1)
    assertThat(moves.asIterable()).isEmpty()
  }

  @Test
  fun delta_empty_hasOneAction() {
    val position = Position(0, 0)
    val board = emptyBoard<Piece<Role>>()
    val actions = board.delta(Position(0, 0), x = 1, y = 2).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Action(position, Delta(1, 2)))
  }

  @Test
  fun delta_includeAdversaries_hasOneAction() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = buildBoard<Piece<Role>> { set(to, adversaryPawn) }
    val actions = board.delta(from, x = 1, y = 1).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Action(from, Delta(1, 1)))
  }

  @Test
  fun delta_notIncludeAdversaries_hasNoActions() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = buildBoard<Piece<Role>> { set(to, adversaryPawn) }
    val actions = board.delta(from, x = 1, y = 1, includeAdversary = false).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun doubleUp_fromIncorrectRow_hasNoActions() {
    val from = Position(3, 3)
    val board = emptyBoard<Piece<Role>>()
    val actions = board.doubleUp(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun doubleUp_withPieceInPath_hasNoActions() {
    val from = Position(0, 6)
    val blocking = Position(0, 5)
    val board = buildBoard<Piece<Role>> { set(blocking, adversaryPawn) }
    val actions = board.doubleUp(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun doubleUp_withoutPieceInPath_hasOneAction() {
    val from = Position(0, 6)
    val board = emptyBoard<Piece<Role>>()
    val actions = board.doubleUp(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Action(from, Delta(0, -2)))
  }

  @Test
  fun sideTakes_noPieces_hasNoActions() {
    val from = Position(1, 1)
    val board = emptyBoard<Piece<Role>>()
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun sideTakes_left_hasOneAction() {
    val from = Position(1, 1)
    val left = Position(0, 0)
    val board = buildBoard<Piece<Role>> { set(left, adversaryPawn) }
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Action(from, Delta(-1, -1)))
  }

  @Test
  fun sideTakes_right_hasOneAction() {
    val from = Position(1, 1)
    val right = Position(2, 0)
    val board = buildBoard<Piece<Role>> { set(right, adversaryPawn) }
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Action(from, Delta(1, -1)))
  }

  @Test
  fun diagonal_isOnTopLeftToBottomRightDiagonal() {
    val from = Position(0, 0)
    val board = emptyBoard<Piece<Role>>()
    val actions = board.diagonals(from).map { it.first }.filter { (_, d) -> d.x != d.y }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun diagonal_hittingAlliedPiece_hasNoActions() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = buildBoard<Piece<Role>> { set(to, alliedPawn) }
    val actions = board.diagonals(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun diagonal_notIncludeAdversaries_hasNoActions() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = buildBoard<Piece<Role>> { set(to, adversaryPawn) }
    val actions = board.diagonals(from, includeAdversary = false).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun diagonal_includeAdversaries_hasOneAction() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = buildBoard<Piece<Role>> { set(to, adversaryPawn) }
    val actions = board.diagonals(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Action(from, Delta(1, 1)))
  }

  @Test
  fun lines_isOnFirstRowOrColumn() {
    val from = Position(0, 0)
    val board = emptyBoard<Piece<Role>>()
    val actions = board.lines(from).map { it.first }.filter { (_, d) -> d.x != 0 && d.y != 0 }
    assertThat(actions.asIterable()).isEmpty()
  }
}
