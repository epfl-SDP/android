package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeatAtLeast
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators

/** An object which contains some convenience parser combinators for any notation. */
object CommonNotationCombinators {
  /** A [Parser] which returns the column in a position. */
  val column = StringCombinators.char().filter { it in 'a'..'h' }.map { it - 'a' }

  /** A [Parser] which returns the row in a position. */
  val row = StringCombinators.digit().map { 8 - it }.filter { it in 0 until Board.Size }

  /** A [Parser] which returns a [Position]. */
  val position = this.computePosition(column, row)

  /**
   * Compute the [position] notation given a [column] and a [row].
   *
   * @param column The [Parser] for the column.
   * @param row The [Parser] for the row.
   * @return A [Parser] for the [Position].
   */
  fun computePosition(
      column: Parser<String, Int>,
      row: Parser<String, Int>
  ): Parser<String, Position> {
    return column.flatMap { x -> row.map { y -> Position(x, y) } }.filter { it.inBounds }
  }

  /** A [Parser] which returns a number of spaces. */
  val spaces = StringCombinators.char(' ').repeatAtLeast(count = 1)

  /** A [Parser] which consumes a number of digits representing an integer number. */
  val integer =
      StringCombinators.digit().repeatAtLeast(count = 1).map {
        it.fold(0) { acc, digit -> acc * 10 + digit }
      }
}
