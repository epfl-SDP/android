package ch.epfl.sdp.mobile.application.speech

typealias Word = String // Piece | Letter | Number

typealias Speech = String // Raw speech Sentence

typealias ChessPiece = String

typealias ChessNumber = String

typealias ChessLetter = String

typealias ChessWord = String? // Filter Result

/**
 * A Chess speech recognition results filters that factors a chess command given a vocabulary
 * dictionary and a set of filtering rules
 *
 * @property chessDictionary chess dictionary rules
 * @property speechRules chess filtering rules
 */
class ChessSpeechRecognitionFilter(
    private val chessDictionary: ChessDictionary = ChessSpeechDictionary,
    private val speechRules: ChessSpeechFilterRules = ChessSpeechFilterRules
) : SpeechRecognitionFilter {

  /**
   * Applies all speechRules on speeches
   * @param speeches list of tokenized speeches
   * @param confidencesScores list of confidence score for recognized speech
   * @return chess keyword string after applying all speechRules, or an empty string if all rules
   * fail
   */
  private fun applyRules(
      speeches: List<List<Word>>,
      confidencesScores: List<Float>,
  ): String {

    val candidates = mutableMapOf<String, Float>()
    for (chessRule in ChessSpeechFilterRules.rulesSet) {
      speeches.forEachIndexed { index, speech ->
        if (chessRule.rule(speech)) {
          candidates[chessRule.chessPiece] = confidencesScores[index]
        }
      }
    }
    return bestCandidate(candidates)
  }

  /**
   * Returns detected chess keyword candidate with best confidence score
   * @param candidates mapping from detected in dictionary chess keywords to their scores
   * @return candidate as a chess keyword
   */
  private fun bestCandidate(candidates: Map<Word, Float>): String {
    return candidates.entries.maxByOrNull { (_, score) -> score }?.key ?: ""
  }

  /**
   * Detects if any word in the speech is within the pieceDictionary and return its equivalent
   * correct chess piece keyword
   * @param words a tokenized speech
   * @param pieceDictionary a mapping from chess piece keywords to their homonyms
   * @return a chess number keyword or an empty string
   */
  private fun detectPieceType(
      words: List<Word>,
      pieceDictionary: Map<ChessPiece, List<String>> = chessDictionary.chessPieces
  ): String {
    return detect(words, pieceDictionary)
  }

  /**
   * Detects if any word in the speech is within the lettersDictionary and return its equivalent
   * correct chess letter keyword
   * @param words a tokenized speech
   * @param lettersDictionary a mapping from chess keyword letter to their homonyms
   * @return a chess number keyword or an empty string
   */
  private fun detectLetter(
      words: List<Word>,
      lettersDictionary: Map<ChessLetter, List<String>> = chessDictionary.letters
  ): String {
    return detect(words, lettersDictionary)
  }

  /**
   * Detects if any word in the speech is within the numberDictionary and return its equivalent
   * correct chess number keyword
   * @param words a tokenized speech
   * @param numbersDictionary a mapping from numbers to their homonyms
   * @return a chess number keyword or an empty string
   */
  private fun detectNumber(
      words: List<Word>,
      numbersDictionary: Map<ChessNumber, List<String>> = chessDictionary.numbers
  ): String {
    return detect(words, numbersDictionary)
  }

  /**
   * Makes a decision of choosing the best candidate [chess piece | letter | number] from a list of
   * speeches by relying on confidence scores
   * @param speeches list of tokenized speech recognizer results
   * @param confidencesScores confidence scores for each speech result in speeches
   * @param detectionBlock a detection method for a certain type of chess keyword (ChessPiece |
   * Number |Letter)
   * @return most confident result [chess piece | letter | number] or empty if could not determine
   * one
   */
  private fun detectFromSpeeches(
      speeches: List<List<Word>>,
      confidencesScores: List<Float>,
      detectionBlock: (List<Word>) -> String,
  ): String {
    val candidates = mutableMapOf<String, Float>()

    speeches.forEachIndexed { index, speech ->
      val candidate = detectionBlock(speech)
      if (candidate.isNotEmpty()) {
        candidates[candidate] = confidencesScores[index]
      }
    }

    return bestCandidate(candidates)
  }

  /**
   * Searches for any matches for a list word given a dictionary of chess keywords
   * @param words a single tokenized speech as a list of words
   * @param dictionary a mapping from chess keyword to its predefined possible homonyms
   * @return a detected key word from dictionary if any, an empty string otherwise
   */
  private fun detect(words: List<Word>, dictionary: Map<String, List<Word>>): String {
    var result = ""
    loop@ for (word in words) {
      for (entry in dictionary) {
        if (entry.value.any { it == word }) {
          result = entry.key
          break@loop
        }
      }
    }
    return result
  }
  /**
   * Tokenizes speeches and put words to lower case
   * @param speeches list of the speech recognizer results
   * @return tokenized lists of speeches ready to be filtered
   */
  private fun tokenizeSpeeches(speeches: List<Speech>): List<List<Word>> {
    return speeches.map { speech -> speech.split(" ").map { word -> word.lowercase() } }
  }

  override fun filterWords(speechResults: List<SpeechRecognitionFilter.Result>): ChessWord {

    val speeches = speechResults.map { it.speech }
    val confidencesScores = speechResults.map { it.confidencesScore }

    val tokenizedSpeeches = tokenizeSpeeches(speeches)

    // Apply piece speech rules
    val pieceFromRule = applyRules(tokenizedSpeeches, confidencesScores)

    // Filter using dictionary
    val pieceFromDictionary =
        detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectPieceType)

    // Prioritizes dictionary filtering over rule
    val piece =
        when (pieceFromDictionary.isNotEmpty()) {
          true -> pieceFromDictionary
          else ->
              when (pieceFromRule.isNotEmpty()) {
                true -> pieceFromRule
                else -> ""
              }
        }

    // Apply filters for letter and number
    val letter = detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectLetter)
    val number = detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectNumber)

    val success = listOf(piece, number, letter).all { it.isNotEmpty() }
    // Build final result
    return if (success)
        StringBuilder()
            .append(piece.capitalize())
            .append("#")
            .append(letter.uppercase())
            .append(number)
            .toString()
    else null
  }

  /** Extension function used to capitalize ChessPiece strings */
  private fun String.capitalize(): String {
    return replaceFirstChar { it.uppercaseChar() }
  }
}
