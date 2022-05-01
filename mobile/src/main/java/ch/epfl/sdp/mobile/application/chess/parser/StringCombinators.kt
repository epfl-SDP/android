package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map

/** An object which contains some convenience parser combinators for [String]. */
object StringCombinators {

  /** Parses the first [Char] of a [String], if it's not empty. */
  fun char(): Parser<String, Char> = Parser {
    if (it.isNotEmpty()) {
      setOf(Parser.Result(it.drop(1), it.first()))
    } else emptySet()
  }

  /**
   * Parses the first [Char] of a [String], if it's not empty and has the provided value.
   *
   * @param value the value that is searched.
   */
  fun char(value: Char): Parser<String, Char> = char().filter { it == value }

  /**
   * Parses the first [Char] of a [String], if it's not empty and has the provided value whether it
   * is lower-case of upper-case.
   *
   * @param value the value that is searched.
   */
  fun charLower(value: Char): Parser<String, Char> = char().filter { it.lowercaseChar() == value }

  /** Parses the first digit of a [String], if it exists. */
  fun digit(): Parser<String, Int> = char().filter { it in '0'..'9' }.map { it - '0' }

  /** Parses the first token of a [String], if it's exists.
   *
   * @param delimiters A vararg of potential [String] expected between tokens
   * @param ignoreCase Whether or not to ignore case in delimiters
   */
  fun token(
      vararg delimiters: String,
      ignoreCase: Boolean = false,
  ): Parser<String, String> = Parser {
    val tokens = it.split(delimiters = delimiters, ignoreCase = ignoreCase, limit = 1)
    if(tokens.isNotEmpty()) {
      val token = tokens.first()
      // TODO: What semantics of split is used?
      val toDrop = it.indexOf(token) + token.length

      setOf(Parser.Result(it.drop(toDrop), token))
    } else {
      emptySet()
    }
  }

  /**
   * Filters the results from this [Parser] which have an empty remaining input to parse,
   * effectively making sure the remaining string is empty.
   *
   * @param O the type of the output of this [Parser].
   */
  fun <O> Parser<String, O>.checkFinished(): Parser<String, O> = Parser {
    parse(it).filterTo(mutableSetOf()) { r -> r.remaining.isEmpty() }
  }
}
