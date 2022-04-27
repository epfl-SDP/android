package ch.epfl.sdp.mobile.ui.speech_recognition

interface SpeechRecognitionFilter {
  fun filterWords(
      speeches: List<String>,
      confidencesScores: List<Double>,
  ): String
}
