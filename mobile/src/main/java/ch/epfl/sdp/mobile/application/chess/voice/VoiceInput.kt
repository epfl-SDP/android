package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Action

/** An object which contains some utilities to transform user's voice input to a [Action]. */
object VoiceInput {

  /**
   * Parses a [List] of possible matching voice input into a [Action].
   *
   * @param input the [List] of candidate voice input.
   * @return Null if none of the input can be transformed into an [Action] return null, otherwise
   * the parsed [Action].
   */
  fun parseInput(input: List<String>): Action? {
    // TODO : parse until action found
    val parsedResult =
        input.firstNotNullOfOrNull { s ->
          // NOTE (Chau) : I let this log here in purpose. This allow use to refine our custom
          // [ChesSpeechEnglishDictionary]. If your command is not recognize and you think that we
          // need to add this in our dictionary, you can reported here
          // https://github.com/epfl-SDP/android/issues/308
          println("DEBUGGING : SPEECH PARSING $s")

          val filtered = VoiceInputCombinator.action().parse(s.lowercase()).firstOrNull()?.output

          filtered
        }

    return parsedResult
  }
}
