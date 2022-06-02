package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map

typealias Token = String

/** An object which contains some convenience parser combinators for [String]. */
object StringCombinators {

  /** Parses the first [Char] of a [String], if it's not empty. */
  fun char(): Parser<String, Char> = Parser {
    if (it.isNotEmpty()) {
      sequenceOf(Parser.Result(it.drop(1), it.first()))
    } else emptySequence()
  }

  /**
   * Parses the first [Char] of a [String], if it's not empty and has the provided value.
   *
   * @param value the value that is searched.
   */
  fun char(value: Char): Parser<String, Char> = char().filter { it == value }

  /** Parses the first digit of a [String], if it exists. */
  fun digit(): Parser<String, Int> = char().filter { it in '0'..'9' }.map { it - '0' }

  /**
   * Parses the first [Token] of a [String].
   *
   * @param delimiter the delimiter between each token.
   */
  fun token(delimiter: Char = ' '): Parser<String, Token> =
      Parser<String, String> {
        if (it.isNotEmpty()) {
          val splitString = it.trim { char -> char == delimiter }.split(delimiter, limit = 2)
          val result = splitString.first()
          val remaining = splitString.drop(1).firstOrNull() ?: ""
          sequenceOf(Parser.Result(remaining, result))
        } else {
          emptySequence()
        }
      }
          .filter { it.isNotEmpty() }

  /**
   * Parses the first [Token] of a [String], if it's not empty and has the provided value.
   *
   * @param value the value that is searched.
   * @param delimiter the delimiter between each token.
   */
  fun token(value: Token, delimiter: Char = ' '): Parser<String, Token> =
      token(delimiter).filter { it == value }

  /**
   * Parse the first [Token] of a [String], and try to find the corresponding [DictKey] homophone.
   *
   * @param DictKey Key type of dictionary.
   * @param dictionary where to look the search the homophone.
   * @param delimiter the delimiter between each token.
   */
  fun <DictKey> convertToken(
      dictionary: Map<DictKey, List<String>>,
      delimiter: Char = ' '
  ): Parser<String, DictKey?> =
      token(delimiter).map { token ->
        for (entry in dictionary) {
          if (entry.value.any { it == token }) {
            return@map entry.key
          }
        }
        return@map null
      }

  /**
   * Filters the results from this [Parser] which have an empty remaining input to parse,
   * effectively making sure the remaining string is empty.
   *
   * @param O the type of the output of this [Parser].
   */
  fun <O> Parser<String, O>.checkFinished(): Parser<String, O> = Parser {
    parse(it).filter { r -> r.remaining.isEmpty() }
  }
}
