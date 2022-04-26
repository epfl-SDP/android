package ch.epfl.sdp.mobile.application.chess.parser

/**
 * An interface which acts as the basis of some parser combinators. A [Parser] takes an [Input], and
 * returns a set of [Result].
 *
 * @param Input the type of the inputs parsed by this [Parser].
 * @param Output the type of the outputs generated by this [Parser].
 */
fun interface Parser<Input, out Output> {

  /**
   * An intermediate result from the [Parser].
   *
   * @param Input the type of the inputs of the parser.
   * @param Output the type of the outputs of the parser.
   * @param remaining the [Input] that can still be parsed.
   * @param output the [Output] generated by the parser.
   */
  data class Result<out Input, out Output>(
      val remaining: Input,
      val output: Output,
  )

  /**
   * Parses the [Input] and results all the possible [Result].
   *
   * @param input the [Input] to the parsed.
   * @return the resulting [Set] of [Result].
   */
  fun parse(input: Input): Set<Result<Input, Output>>
}