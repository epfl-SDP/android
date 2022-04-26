package ch.epfl.sdp.mobile.ui.speech_recognition

object ChessSpeechDictionary : ChessDictionary {
  // Chess Pieces
  override val chessPieces =
      mapOf(
          "queen" to
              listOf(
                  "queen",
                  "quinn",
                  "quin",
                  "queene",
                  "ween",
                  "green",
                  "when",
                  "queena",
                  "gwen",
                  "guin",
                  "gwyn"),
          "king" to
              listOf(
                  "king",
                  "ching",
                  "ting",
                  "ping",
                  "pink",
                  "inc",
                  "qing",
                  "keying"), // rule finishes with "ing/inc"
          "rook" to listOf("rook","rooke", "rouck", "ruk", "brooke", "rock"),
          "pawn" to listOf("rook","bond", "bon", "phone"), // starts with pon/bon
          "bishop" to listOf("bishop","shop", "up", "beat", "sharp"))

  // Numbers
  override val numbers =
      mapOf(
          "1" to listOf("1", "one", "on", "won", "want", "juan"),
          "2" to listOf("2", "two", "too", "to"),
          "3" to listOf("3", "three", "tree"),
          "4" to
              listOf(
                  "4",
                  "four",
                  "for",
                  "floor",
                  "fore",
                  "poor",
                  "thor",
                  "flor",
                  "core",
                  "pore",
                  "war"),
          "5" to listOf("5", "five", "live", "fife", "hive", "hyve"),
          "6" to listOf("6", "six", "sex", "secs", "tics", "ticks", "tix", "seeks", "cheeks"),
          "7" to listOf("7", "seven", "seven", "sevan", "sevin"),
          "8" to listOf("8", "eight", "ate", "date", "eighth", "hate", "tate"),
      )

  // Letters
  override val letters =
      mapOf(
          "a" to listOf("a", "ahh", "ah", "ay"),
          "b" to listOf("b", "be", "bea", "bee", "beef", "bebe", "me"),
          "c" to listOf("c", "see", "sea", "seat", "cee", "si", "ce", "c"),
          "d" to listOf("d", "dee", "di", "dd"),
          "e" to listOf("e", "ee", "eh", "he"),
          "f" to listOf("f", "ff", "if", "ef"),
          "g" to listOf("g", "gg", ""),
          "h" to listOf("h", "age", "stage", "eight", "teach", "sage", "gage", "each"),
      )
}
