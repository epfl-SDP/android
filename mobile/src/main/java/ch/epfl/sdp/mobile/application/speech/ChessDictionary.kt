package ch.epfl.sdp.mobile.application.speech

/**
 * Interface representing the vocabulary of chess move commands chessPieces mapping of chess pieces
 * keywords to their homophones letters mapping of chess letter keywords to their homophones
 */
interface ChessDictionary {
  /** chessPieces mapping of chess pieces keywords to their homophones */
  val chessPieces: Map<String, List<String>>
  /** numbers mapping of chess number keywords to their homophones */
  val numbers: Map<String, List<String>>
  /** letters mapping of chess letter keywords to their homophones */
  val letters: Map<String, List<String>>
  /** placements all possible combinations of chess board placement */
  val placements: Set<String>
}
