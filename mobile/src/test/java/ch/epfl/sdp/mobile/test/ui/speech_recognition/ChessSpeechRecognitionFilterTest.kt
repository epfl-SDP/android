package ch.epfl.sdp.mobile.test.ui.speech_recognition

import ch.epfl.sdp.mobile.ui.speech_recognition.ChessSpeechRecognitionFilter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Test

class ChessSpeechRecognitionFilterTest {

  @Test
  fun given_randomSpeech_when_filtered_thenOutputIsUndefined() {
    val speeches = listOf("hello")
    val confidenceScores = listOf(1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("!!#@$"))
  }
  @Test
  fun given_queenSpeeches_when_filtered_then_outputCorrectResults() {
    val speeches = listOf("queen bee six", "queen v sex", "queen music")
    val confidenceScores = listOf(1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Queen#B6"))
  }

  @Test
  fun given_kingSpeeches_when_filtered_and_rulesApplied_then_outputIsCorrect() {
    val speeches = listOf("king c 2", "king city to", "thing C2")
    val confidenceScores = listOf(1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("King#C2"))
  }

  @Test
  fun given_pawnSpeeches_when_filtered_and_rulesApplied_then_outputIsCorrect() {

    val speeches = listOf("phone a 1", "born a one", "bourne a1")
    val confidenceScores = listOf(1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Pawn#A1"))
  }

  @Test
  fun given_rookSpeeches_when_filtered_and_rulesApplied_then_outputIsCorrect() {

    val speeches = listOf("baruch C7", "baruuk c7", "brooke c seven")
    val confidenceScores = listOf(1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Rook#C7"))
  }

  @Test
  fun given_bishopSpeeches_when_filtered_and_rulesApplied_then_outputIsCorrect() {

    val speeches = listOf("bishop 8h", "bishop eight age", "bishop h h")
    val confidenceScores = listOf(1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Bishop#H8"))
  }

  @Test
  fun given_closelyRelatedSpeeches_when_filtered_thenOutputHasHigherScore() {
    val speeches = listOf("queen e 2", "queen h 8 ")
    val confidenceScores = listOf(1.0, 2.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Queen#H8"))
  }

  @Test
  fun given_notInDictionaryPawnSpeeches_when_pawnRuleApplied_thenOutputIsCorrect() {
    val speeches = listOf("Pond a 1", "Boned a 1")
    val confidenceScores = listOf(1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Pawn#A1"))
  }

  @Test
  fun given_notInDictionaryRookSpeeches_when_rookRuleApplied_thenOutputIsCorrect() {
    val speeches = listOf("..uk", "..uch", "..ouk", "..och")
    val confidenceScores = listOf(1.0, 1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("Rook#@$"))
  }

  @Test
  fun given_notInDictionaryKingSpeeches_when_kingRuleApplied_thenOutputIsCorrect() {
    val speeches = listOf("..inc", "..ing", "..ink")
    val confidenceScores = listOf(1.0, 1.0, 1.0)

    val chessFilter = ChessSpeechRecognitionFilter()
    val result = chessFilter.filterWords(speeches, confidenceScores)

    assertThat(result, IsEqual("King#@$"))
  }
}
