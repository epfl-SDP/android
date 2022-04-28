package ch.epfl.sdp.mobile.ui.speech_recognition

/** Filtering interface for speech recognizer results */
interface SpeechRecognitionFilter {
  /**
   * @param speeches list of recognized speeches
   * @param confidencesScores assigned scores to each recognized speech
   * @return filtered results
   */
  fun filterWords(
      speeches: List<String>,
      confidencesScores: List<Double>,
  ): String
}
