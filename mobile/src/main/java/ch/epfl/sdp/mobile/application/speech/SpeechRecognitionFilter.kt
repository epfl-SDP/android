package ch.epfl.sdp.mobile.application.speech

/** Filtering interface for speech recognizer results */
interface SpeechRecognitionFilter {

  /**
   * Data class that encapsulate a speech result with its confidence score
   * @property speech recognized speech
   * @property confidencesScore assigned score to the recognized speech
   */
  data class Result(val speech: String, val confidencesScore: Float)

  /**
   * Filter the list of results to determine a valid chess move
   * @param speechResults list of speech recognizer results
   * @return filtered results or an empty string if it could not determine the chess move
   */
  fun filterWords(speechResults: List<Result>): String?
}
