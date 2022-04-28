package ch.epfl.sdp.mobile.ui.speech_recognition

import java.lang.StringBuilder

typealias Word = String // Piece | Letter | Number

typealias Speech = String // Raw speech Sentence

typealias ChessPiece = String

typealias ChessWord = String // Filter Result

class ChessSpeechRecognitionFilter(
    private val chessDictionary: ChessDictionary = ChessSpeechDictionary,
    private val speechRules: ChessSpeechFilterRules = ChessSpeechFilterRules
) : SpeechRecognitionFilter {

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

  private fun detectPieceType(
      words: List<Word>,
      pieceDictionary: Map<ChessPiece, List<String>> = chessDictionary.chessPieces
  ): String {
    return detect(words, pieceDictionary)
  }

  private fun detectLetter(
      words: List<Word>,
      lettersDictionary: Map<ChessPiece, List<String>> = chessDictionary.letters
  ): String {
    return detect(words, lettersDictionary)
  }

  private fun detectNumber(
      words: List<Word>,
      numbersDictionary: Map<ChessPiece, List<String>> = chessDictionary.numbers
  ): String {
    return detect(words, numbersDictionary)
  }

  private fun detectFromSpeeches(
      speeches: List<List<Word>>,
      confidencesScores: List<Double>,
      block: (List<Word>) -> String,
  ): String {
    val candidates = mutableMapOf<String, Double>()

    speeches.forEachIndexed { index, speech ->
      val candidate = block(speech)
      if (candidate.isNotEmpty()) {
        candidates[candidate] = confidencesScores[index]
      }
    }

    return bestCandidate(candidates)
  }
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

  private fun tokenizeSpeeches(speeches: List<Speech>): List<List<Word>> {
    return speeches.map { speeche -> speeche.split(" ").map { word -> word.lowercase() } }
  }

  override fun filterWords(speeches: List<String>, confidencesScores: List<Double>): ChessWord {
    val resultBuilder: StringBuilder = StringBuilder("")
    val tokenizedSpeeches = tokenizeSpeeches(speeches)
    // Apply piece speech rules
    val pieceFromRule = applyRules(tokenizedSpeeches, confidencesScores)
    val pieceFromDictionary =
        detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectPieceType)

    // Prioritize dictionary filtering over rule
    val piece =
        when (pieceFromDictionary.isNotEmpty()) {
          true -> pieceFromDictionary
          else ->
              when (pieceFromRule.isNotEmpty()) {
                true -> pieceFromRule
                else -> "!!"
              }
        }

    // Apply filter
    val letter =
        detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectLetter).ifEmpty { "@" }
    val number =
        detectFromSpeeches(tokenizedSpeeches, confidencesScores, this::detectNumber).ifEmpty { "$" }

    // Make result
    return resultBuilder
        .append(piece.capitalize())
        .append("#")
        .append(letter.uppercase())
        .append(number)
        .toString()
  }

  private fun String.capitalize(): String {
    return this[0].uppercase() + this.substring(1)
  }
}
