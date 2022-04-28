package ch.epfl.sdp.mobile.ui.speech_recognition

/**
 * Interface representing the vocabulary of chess move commands
 * @property chessPieces mapping of chess pieces keywords to their homonyms
 * @property numbers mapping of chess number keywords to their homonyms
 * @property letters mapping of chess letter keywords to their homonyms
 * @property placements all possible combinations of chess board placement
 */
interface ChessDictionary {
  val chessPieces: Map<String, List<String>>
  val numbers: Map<String, List<String>>
  val letters: Map<String, List<String>>
  val placements: List<String>
}
