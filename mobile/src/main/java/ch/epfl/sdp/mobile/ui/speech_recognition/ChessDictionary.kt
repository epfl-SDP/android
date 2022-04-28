package ch.epfl.sdp.mobile.ui.speech_recognition

interface ChessDictionary {
  val chessPieces: Map<String, List<String>>
  val moves: List<String>
  val numbers: Map<String, List<String>>
  val letters: Map<String, List<String>>
}
