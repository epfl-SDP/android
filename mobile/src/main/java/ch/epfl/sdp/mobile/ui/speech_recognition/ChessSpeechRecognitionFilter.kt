package ch.epfl.sdp.mobile.ui.speech_recognition

import java.lang.StringBuilder

typealias Word = String // Piece | Letter | Number

typealias Speech = String // Raw speech Sentence

typealias ChessPiece = String

typealias ChessNumber = String

typealias ChessLetter = String

typealias ChessWord = String // Filter Result

/**
 */
class ChessSpeechRecognitionFilter(
    private val chessDictionary: ChessDictionary = ChessSpeechDictionary,
    private val speechRules: ChessSpeechFilterRules = ChessSpeechFilterRules
) : SpeechRecognitionFilter {

  /**
   */
  private fun applyRules(
      speeches: List<List<Word>>,
      confidencesScores: List<Double>,
  ): String {

    val candidates = mutableMapOf<String, Double>()
    for (chessRule in speechRules.rulesSet) {
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
  private fun bestCandidate(candidates: Map<Word, Double>): String {
    if (candidates.isEmpty()) {
      return ""
    }

    if (candidates.size == 1) {
      return candidates.entries.first().key
    }

    var bestCandidate = candidates.entries.first()
    for (candidate in candidates) {
      if (candidate.value > bestCandidate.value) {
        bestCandidate = candidate
      }
    }
    return bestCandidate.key
  }

  /**
   * Detects if any word in the speech is within the numberDictionary and return its equivalent
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
   * Detects if any word in the speech is within the numberDictionary and return its equivalent
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
   * Makes a decision of choosing a conform by relying on confidence scores
   * @param speeches list of tokenized speech recognizer results
   * @param confidencesScores confidence scores for each speech result in speeches
   * @param detectionBlock a detection method for a certain type of chess keyword (ChessPiece,
   * Number, Letter)
   * @return a result with confidence or empty if could not determine one
   */
  private fun detectFromSpeeches(
      speeches: List<List<Word>>,
      confidencesScores: List<Double>,
      detectionBlock: (List<Word>) -> String,
  ): String {
    val candidates = mutableMapOf<String, Double>()

    speeches.forEachIndexed { index, speech ->
      val candidate = detectionBlock(speech)
      if (candidate.isNotEmpty()) {
        candidates[candidate] = confidencesScores[index]
      }
    }

    return bestCandidate(candidates)
  }

  /**
   * Searches for any matches of for a list word given a dictionary of chess keywords
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
    return speeches.map { speeche -> speeche.split(" ").map { word -> word.lowercase() } }
  }

  override fun filterWords(speeches: List<String>, confidencesScores: List<Double>): ChessWord {

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
                else -> "!!"
              }
        }

    // Apply filters for letter and number
    val letter =
        detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectLetter).ifEmpty { "@" }
    val number =
        detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectNumber).ifEmpty { "$" }

    // Build final result
    return StringBuilder()
        .append(piece.capitalize())
        .append("#")
        .append(letter.uppercase())
        .append(number)
        .toString()
  }

  /** Extension function used to capitalize ChessPiece strings */
  private fun String.capitalize(): String {
    return this[0].uppercase() + this.substring(1)
  }
}
