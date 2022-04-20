package ch.epfl.sdp.mobile.test.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Rank.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.engine.rules.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Role.Adversary
import ch.epfl.sdp.mobile.application.chess.engine.rules.Role.Allied
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovesTest {

  private val adversaryKing = Piece(Adversary, King, PieceIdentifier(0))
  private val adversaryPawn = Piece(Adversary, Pawn, PieceIdentifier(0))
  private val adversaryRook = Piece(Adversary, Rook, PieceIdentifier(0))
  private val alliedKing = Piece(Allied, King, PieceIdentifier(0))
  private val alliedPawn = Piece(Allied, Pawn, PieceIdentifier(0))
  private val alliedRook = Piece(Allied, Rook, PieceIdentifier(0))

  @Test
  fun delta_outOfBounds_hasNoActions() {
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val moves = board.delta(Position(0, 0), x = -1, y = -1)
    assertThat(moves.asIterable()).isEmpty()
  }

  @Test
  fun delta_empty_hasOneAction() {
    val position = Position(0, 0)
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val actions = board.delta(Position(0, 0), x = 1, y = 2).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(position, Delta(1, 2)))
  }

  @Test
  fun delta_includeAdversaries_hasOneAction() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(to, adversaryPawn) })
    val actions = board.delta(from, x = 1, y = 1).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(from, Delta(1, 1)))
  }

  @Test
  fun delta_notIncludeAdversaries_hasNoActions() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(to, adversaryPawn) })
    val actions = board.delta(from, x = 1, y = 1, includeAdversary = false).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun given_pawnOnPenultimateRow_when_movesUp_then_canPromote() {
    val from = Position(0, 1)
    val to = Position(0, 0)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(from, alliedPawn) })
    val actions =
        board.delta(from = from, x = 0, y = -1, includeAdversary = false, promotionAllowed = true)
            .map { it.first }
    assertThat(actions.toSet())
        .containsExactly(
            Action.Promote(from, to, Bishop),
            Action.Promote(from, to, Knight),
            Action.Promote(from, to, Queen),
            Action.Promote(from, to, Rook),
        )
  }

  @Test
  fun doubleUp_fromIncorrectRow_hasNoActions() {
    val from = Position(3, 3)
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val actions = board.doubleUp(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun doubleUp_withPieceInPath_hasNoActions() {
    val from = Position(0, 6)
    val blocking = Position(0, 5)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(blocking, adversaryPawn) })
    val actions = board.doubleUp(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun doubleUp_withoutPieceInPath_hasOneAction() {
    val from = Position(0, 6)
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val actions = board.doubleUp(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(from, Delta(0, -2)))
  }

  @Test
  fun sideTakes_noPieces_hasNoActions() {
    val from = Position(1, 1)
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun sideTakes_left_hasOneMoveAction() {
    val from = Position(2, 2)
    val left = Position(1, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(left, adversaryPawn) })
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(from, Delta(-1, -1)))
  }

  @Test
  fun given_pawnOnPenultimateRow_when_adversaryOnLeft_then_canPromote() {
    val from = Position(1, 1)
    val left = Position(0, 0)
    val board =
        sequenceOf(
            buildBoard<Piece<Role>> {
              set(from, alliedPawn)
              set(left, adversaryPawn)
            },
        )
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.toSet())
        .containsExactly(
            Action.Promote(from, left, Bishop),
            Action.Promote(from, left, Knight),
            Action.Promote(from, left, Queen),
            Action.Promote(from, left, Rook),
        )
  }

  @Test
  fun given_noPawn_when_adversaryOnLeft_then_canNotPromote() {
    val from = Position(1, 1)
    val left = Position(0, 0)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(left, adversaryPawn) })

    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.toSet()).isEmpty()
  }

  @Test
  fun sideTakes_right_hasOneMoveAction() {
    val from = Position(2, 2)
    val right = Position(3, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(right, adversaryPawn) })
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(from, Delta(1, -1)))
  }

  @Test
  fun given_pawnOnPenultimateRow_when_adversaryOnRight_then_canPromote() {
    val from = Position(1, 1)
    val right = Position(2, 0)
    val board =
        sequenceOf(
            buildBoard<Piece<Role>> {
              set(from, alliedPawn)
              set(right, adversaryPawn)
            },
        )
    val actions = board.sideTakes(from).map { it.first }
    assertThat(actions.toSet())
        .containsExactly(
            Action.Promote(from, right, Bishop),
            Action.Promote(from, right, Knight),
            Action.Promote(from, right, Queen),
            Action.Promote(from, right, Rook),
        )
  }

  @Test
  fun diagonal_isOnTopLeftToBottomRightDiagonal() {
    val from = Position(0, 0)
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val actions = board.diagonals(from).map { it.first }.filter { (_, d) -> d.x != d.y }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun diagonal_hittingAlliedPiece_hasNoActions() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(to, alliedPawn) })
    val actions = board.diagonals(from).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun diagonal_notIncludeAdversaries_hasNoActions() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(to, adversaryPawn) })
    val actions = board.diagonals(from, includeAdversary = false).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun diagonal_includeAdversaries_hasOneAction() {
    val from = Position(0, 0)
    val to = Position(1, 1)
    val board = sequenceOf(buildBoard<Piece<Role>> { set(to, adversaryPawn) })
    val actions = board.diagonals(from).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(from, Delta(1, 1)))
  }

  @Test
  fun lines_isOnFirstRowOrColumn() {
    val from = Position(0, 0)
    val board = sequenceOf(emptyBoard<Piece<Role>>())
    val actions = board.lines(from).map { it.first }.filter { (_, d) -> d.x != 0 && d.y != 0 }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_missingKing_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard { set(Position(7, 7), alliedRook) },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_missingRook_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard { set(Position(4, 7), alliedKing) },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_emptyHistory_isSuccessful() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(7, 7), alliedRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(Position(4, 7), Delta(2, 0)))
  }

  @Test
  fun castling_blockingPiece_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(5, 7), adversaryPawn)
                set(Position(7, 7), alliedRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_movedKing_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(7, 7), alliedRook)
              },
          )
          yield(
              buildBoard {
                set(Position(3, 7), alliedKing)
                set(Position(7, 7), alliedRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_movedRook_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(7, 7), alliedRook)
              },
          )
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(7, 6), alliedRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_adversaryKing_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), adversaryKing)
                set(Position(7, 7), alliedRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_adversaryRook_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(7, 7), adversaryRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_alliedButNotKing_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedPawn)
                set(Position(7, 7), alliedRook)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun castling_alliedButNotRook_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(4, 7), alliedKing)
                set(Position(7, 7), alliedPawn)
              },
          )
        }
    val actions = board.rightCastling().map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_emptyHistory_isSuccessful() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(0, 3), alliedPawn)
                set(Position(1, 3), adversaryPawn)
              },
          )
          yield(
              buildBoard {
                set(Position(0, 3), alliedPawn)
                set(Position(1, 1), adversaryPawn)
              },
          )
        }
    val actions = board.enPassant(Position(0, 3), Delta(1, 0)).map { it.first }
    assertThat(actions.asIterable()).containsExactly(Move(Position(0, 3), Delta(1, -1)))
  }

  @Test
  fun enPassant_inBetweenMove_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(0, 3), alliedPawn)
                set(Position(1, 3), adversaryPawn)
              },
          )
          yield(
              buildBoard {
                set(Position(0, 3), alliedPawn)
                set(Position(1, 2), adversaryPawn)
              },
          )
        }
    val actions = board.enPassant(Position(0, 3), Delta(1, 0)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_badRow_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard {
                set(Position(0, 4), alliedPawn)
                set(Position(1, 4), adversaryPawn)
              },
          )
        }
    val actions = board.enPassant(Position(0, 4), Delta(1, 0)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_noNeighbour_isNotAllowed() {
    val board = sequence<Board<Piece<Role>>> { yield(emptyBoard()) }
    val actions = board.enPassant(Position(0, 3), Delta(1, 0)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_neighbourIsAllied_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard { set(Position(1, 3), alliedPawn) },
          )
        }
    val actions = board.enPassant(Position(0, 3), Delta(1, 0)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_neighbourIsNotPawn_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard { set(Position(1, 3), adversaryRook) },
          )
        }
    val actions = board.enPassant(Position(0, 3), Delta(1, 0)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_neighbourIsOutOfBounds1_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard { set(Position(7, 0), adversaryPawn) },
          )
        }
    // This isn't really a valid Delta, but our enPassant implementation allows it.
    val actions = board.enPassant(Position(6, 3), Delta(1, -3)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }

  @Test
  fun enPassant_neighbourIsOutOfBounds2_isNotAllowed() {
    val board =
        sequence<Board<Piece<Role>>> {
          yield(
              buildBoard { set(Position(7, 1), adversaryPawn) },
          )
        }
    // This isn't really a valid Delta, but our enPassant implementation allows it.
    val actions = board.enPassant(Position(6, 3), Delta(1, -2)).map { it.first }
    assertThat(actions.asIterable()).isEmpty()
  }
}
