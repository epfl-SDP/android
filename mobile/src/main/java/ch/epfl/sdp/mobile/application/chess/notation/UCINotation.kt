package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.UCINotationCombinators.uciActions

/**
 * An object which contains some utilities to transform games in UCI notation, and vice-versa.
 */
object UCINotation {

  /**
   * Parses a [List] of [Action] from the provided [String] text in UCI notation.
   *
   * @param text the [String] that should be parsed.
   * @return the [List] of [Action] that was found.
   */
  fun parseActions(text: String): List<Action> {
    return uciActions().parse(input = text).single().output
  }
}
