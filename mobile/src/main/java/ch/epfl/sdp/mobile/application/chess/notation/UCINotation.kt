package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.notation.UCINotationCombinators.actions

/** An object which contains some utilities to transform UCI notation to a [List] of [Action]s. */
object UCINotation {

  /**
   * Parses a [List] of [Action] from the provided [String] text in UCI notation.
   *
   * @param text the [String] that should be parsed.
   * @return the [List] of [Action] that was found.
   */
  fun parseActions(text: String): List<Action>? {
    return actions().parse(input = text).firstOrNull()?.output
  }
}
