package ch.epfl.sdp.mobile.test.application.chess.engine.truth

import ch.epfl.sdp.mobile.application.chess.engine.Piece
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth.assertAbout

/**
 * A [Subject] about the [Piece] of a board.
 *
 * @param C the color of the piece.
 * @param metadata the [FailureMetadata] for this [Subject].
 * @param actual the [Piece] under test.
 */
class PieceSubject<C>
private constructor(
    metadata: FailureMetadata,
    private val actual: Piece<C>?,
) : Subject(metadata, actual) {

  /**
   * Fails if the subject's rank or color are different from the expected rank or color.
   *
   * @param expected the [Piece] that the subject is compared to.
   */
  fun isSameRankAndColor(expected: Piece<C>?) {
    check("rank").that(actual?.rank).isEqualTo(expected?.rank)
    check("color").that(actual?.color).isEqualTo(expected?.color)
  }

  companion object {

    /**
     * Returns a [Subject.Factory] for [PieceSubject].
     *
     * @param C the color of the piece.
     */
    fun <C> pieces(): Factory<PieceSubject<C>, Piece<C>?> = Factory { metadata, actual ->
      PieceSubject(metadata, actual)
    }

    /**
     * An entry point to make fluent assertions about a [Piece].
     *
     * @param C the color of the piece.
     * @param actual the [Piece] under test.
     * @return a [PieceSubject].
     */
    @JvmStatic fun <C> assertThat(actual: Piece<C>?) = assertAbout(pieces<C>()).that(actual)
  }
}
