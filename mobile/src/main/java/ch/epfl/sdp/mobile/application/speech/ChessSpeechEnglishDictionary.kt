package ch.epfl.sdp.mobile.application.speech

/**
 * Currently used chess dictionary for homophones chess keywords.
 *
 * Some homophones is provided by :
 * - (Uk pronunciation) : http://www.singularis.ltd.uk/bifroest/misc/homophones.html
 * - https://www.homophone.com/
 */
object ChessSpeechEnglishDictionary : ChessDictionary {
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
                  "gwyn",
                  "quean"),
          "king" to
              listOf(
                  "king",
                  "ching",
                  "ting",
                  "ping",
                  "pink",
                  "qing",
                  "keying"), // rule finishes with "ing/inc"
          "rook" to listOf("rook", "rooke", "rouck", "ruk", "brooke", "rock", "route"),
          "pawn" to listOf("pawn", "bond", "bon", "phone", "fun", "porn"), // starts with pon/bon
          "bishop" to listOf("bishop", "shop", "up", "beat", "sharp"),
          "knight" to listOf("knight"))
  // Numbers
  override val numbers =
      mapOf(
          '1' to listOf("1", "one", "on", "won", "want", "juan"),
          '2' to listOf("2", "two", "too", "to"),
          '3' to listOf("3", "three", "tree"),
          '4' to
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
          '5' to listOf("5", "five", "live", "fife", "hive", "hyve"),
          '6' to
              listOf("6", "six", "sex", "secs", "tics", "ticks", "tix", "seeks", "cheeks", "sics"),
          '7' to listOf("7", "seven", "sevan", "sevin"),
          '8' to listOf("8", "eight", "ate", "date", "eighth", "hate", "tate", "ait"),
      )

  // Letters
  override val letters =
      mapOf(
          'a' to
              mutableListOf("a", "ahh", "ah", "ay").apply {
                this.addAll(numbers.getOrDefault('8', emptyList()))
              },
          'b' to listOf("b", "be", "bea", "bee", "beef", "bebe", "me"),
          'c' to listOf("c", "see", "sea", "seat", "cee", "si", "ce"),
          'd' to listOf("d", "dee", "di", "dd"),
          'e' to listOf("e", "ee", "eh", "he"),
          'f' to listOf("f", "ff", "if", "ef", "s"),
          'g' to listOf("g", "gg"),
          'h' to listOf("h", "age", "stage", "eight", "teach", "sage", "gage", "each"),
      )

  override val actions = mapOf("to" to listOf("to", "two", "too", "to", "2"))

  override val placements: Set<String> =
      letters.keys.flatMap { letter -> numbers.keys.map { num -> "" + letter + num } }.toSet()
}
