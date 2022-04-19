package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.Parser.Result

/** An object which contains some convenience parser combinators. */
object Combinators {

  /** Returns a [Parser] which can't parse the current input. */
  fun <T> failure(): Parser<T, Nothing> = Parser { emptySet() }

  /**
   * Returns a [Parser] which always succeeds, and applies the identity function to the input.
   *
   * @param I the type of the input.
   * @param R the type of the result.
   * @param result the result.
   */
  fun <I, R> success(result: R): Parser<I, R> = Parser { setOf(Result(it, result)) }

  /**
   * Returns a [Parser] which combines [this] and an [other] [Parser].
   *
   * @param I the type of the input.
   * @param O the type of the result.
   * @receiver the first [Parser].
   * @param other the second [Parser].
   */
  infix fun <I, O> Parser<I, O>.or(other: Parser<I, O>): Parser<I, O> = combine(this, other)

  /**
   * Combines multiple [Parser] and returns the union of their results.
   *
   * @param I the type of the input.
   * @param O the type of the result.
   * @param parsers the [Parser] which are combined.
   */
  fun <I, O> combine(vararg parsers: Parser<I, O>): Parser<I, O> = Parser { input ->
    parsers.asSequence().map { it.parse(input) }.fold(mutableSetOf()) { acc, set ->
      acc.apply { addAll(set) }
    }
  }

  /**
   * Returns a [Parser] which maps the results of the existing parser using a provided function.
   *
   * @param I the type of the input.
   * @param O1 the type of the output of the original parser.
   * @param O2 the type of the output of the resulting parser.
   * @receiver the [Parser] which will be mapped.
   * @param f the function used to map the results.
   */
  inline fun <I, O1, O2> Parser<I, O1>.map(
      crossinline f: (O1) -> O2,
  ): Parser<I, O2> = Parser {
    parse(it).mapTo(mutableSetOf()) { r -> Result(r.remaining, f(r.output)) }
  }

  /**
   * Returns a [Parser] which flat-maps the results of the existing parser using a provided
   * function.
   *
   * @param I the type of the input.
   * @param O1 the type of the output of the original parser.
   * @param O2 the type of the output of the resulting parser.
   * @receiver the [Parser] which will be mapped.
   * @param f the function used to flat-map the current output to select the next combinator.
   */
  inline fun <I, O1, O2> Parser<I, O1>.flatMap(
      crossinline f: (O1) -> Parser<I, O2>,
  ): Parser<I, O2> = Parser {
    parse(it).flatMapTo(mutableSetOf()) { r -> f(r.output).parse(r.remaining) }
  }

  /**
   * Returns a default value if the [Parser] did not manage to find any valid result.
   *
   * @param I the type of the input.
   * @param O the type of the output.
   * @param lazyDefaultValue the lambda which computes the default value.
   */
  inline fun <I, O> Parser<I, O>.orElse(
      crossinline lazyDefaultValue: (I) -> O,
  ): Parser<I, O> = Parser { parse(it).ifEmpty { setOf(Result(it, lazyDefaultValue(it))) } }

  /**
   * Returns a [Parser] which only keeps results which match the predicate.
   *
   * @param I the type of the input.
   * @param O the type of the output.
   * @param keepIf a predicate which must return true iff the result should be kept.
   */
  fun <I, O> Parser<I, O>.filter(keepIf: (O) -> Boolean): Parser<I, O> = flatMap {
    if (keepIf(it)) success(it) else failure()
  }
}
