package ch.epfl.sdp.mobile.ui.speech_recognition

import java.lang.StringBuilder

typealias Word = String // Piece // letter // number

typealias Speech = String //

typealias ChessWord = String // Result

typealias Rule = (String) -> Boolean // Predicate Rule

class ChessSpeechRecognitionFilter(
    private val speeches: List<Speech>,
    private val confidences_scores: List<Double>,
    private val chessDictionary: ChessDictionary = ChessSpeechDictionary
) : SpeechRecognitionFilter {




  private fun applyRules(rule: SpeechFilterRules): String {
    return ""
  }

  private fun detectPieceType(
      words: List<Word>,
      pieceDictionary: Map<String, List<String>> = chessDictionary.chessPieces
  ): String? {

    return detect(words, pieceDictionary)
  }

  private fun detectLetter(
      words: List<Word>,
      lettersDictionary: Map<String, List<String>> = chessDictionary.letters
  ): String? {
    return detect(words, lettersDictionary)
  }

  private fun detectNumber(
      words: List<Word>,
      numbersDictionary: Map<String, List<String>> = chessDictionary.numbers
  ): String? {
    return detect(words, numbersDictionary)
  }

  fun applyFilters(): ChessWord {
    return ""
  }

  private fun detect(words: List<Word>, dictionary: Map<String, List<String>>): String? {
    var result: String? = null
    loop@ for (word in words) {
      for (entry in dictionary) {
        if (entry.value.any { it -> it == word }) {
          result = entry.key
          break@loop
        }
      }
    }
    return result
  }

  private fun tokenizeSpeeches(speeches: List<Speech>): List<List<Word>> {
//    return speeches
//        .fold<Speech, MutableMap<Speech, List<Word>>>(
//            initial = mutableMapOf(),
//            operation = { acc, s ->
//              acc[s] = s.split(" ").map { it.lowercase() }
//              return acc
//            })
//        .toMap()

      return speeches.map { it.split(" ") }
  }

  override fun filterWords(speeches: List<String>): ChessWord {
      val resultBuilder: StringBuilder = StringBuilder("")
      val tokenizedSpeeches = tokenizeSpeeches(speeches)
      //..
      return resultBuilder.toString()
  }
}

