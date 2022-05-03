package ch.epfl.sdp.mobile.test.application.chess.engine.truth

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.test.application.chess.engine.truth.PieceSubject.Companion.pieces
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth.assertAbout

/**
 * A [Subject] about a [Board] of [Piece].
 *
 * @param C the color of the pieces.
 * @param metadata the [FailureMetadata] for this [Subject].
 * @param actual the [Board] under test.
 */
class PieceBoardSubject<C : Any>
private constructor(
    metadata: FailureMetadata,
    private val actual: Board<Piece<C>>?,
) : Subject(metadata, actual) {

  /**
   * Fails if the subject's pieces are not positioned in the same positions as the expected board.
   *
   * @param expected the [Board] of [Piece] that the subject is compared to.
   */
  fun isSamePiecePosition(expected: Board<Piece<C>>?) {
    for (position in Position.all()) {
      check("get($position)")
          .about(pieces<C>())
          .that(actual?.get(position))
          .isSameRankAndColor(expected?.get(position))
    }
  }

  companion object {

    /**
     * Returns a [Subject.Factory] for [PieceBoardSubject].
     *
     * @param C the color of the pieces.
     */
    fun <C : Any> pieceBoards(): Factory<PieceBoardSubject<C>, Board<Piece<C>>> =
        Factory { metadata, actual ->
          PieceBoardSubject(metadata, actual)
        }

    /**
     * An entry point to make fluent assertions about a [Board] of [Piece].
     *
     * @param C the color of the piece.
     * @param actual the [Piece] under test.
     * @return a [PieceBoardSubject].
     */
    @JvmStatic
    fun <C : Any> assertThat(actual: Board<Piece<C>>?) = assertAbout(pieceBoards<C>()).that(actual)
  }
}
