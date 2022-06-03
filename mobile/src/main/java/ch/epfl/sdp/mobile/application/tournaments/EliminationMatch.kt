package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ChessMetadata

/**
 * A data class representing an elimination match.
 *
 * @property id the identifier of this match.
 * @property depth the depth of this match.
 * @property whiteName the name of the white player.
 * @property blackName the name of the black player.
 * @property status the [Status] of the match.
 */
data class EliminationMatch(
    val id: String,
    val depth: Int,
    val whiteName: String,
    val blackName: String,
    val status: Status,
) {

  /** An enumeration representing the possible statuses of an [EliminationMatch]. */
  enum class Status {
    WhiteWon,
    BlackWon,
    Drawn,
    None,
  }
}

/** Returns the [EliminationMatch] from this [ChessDocument]. */
fun ChessDocument.toEliminationMatch(): EliminationMatch {
  val whiteName = metadata?.whiteName ?: "Waiting for first move..."
  val blackName = metadata?.blackName ?: "Waiting for first move..."
  val status =
      when (metadata?.status) {
        ChessMetadata.Stalemate -> EliminationMatch.Status.Drawn
        ChessMetadata.WhiteWon -> EliminationMatch.Status.WhiteWon
        ChessMetadata.BlackWon -> EliminationMatch.Status.BlackWon
        else -> EliminationMatch.Status.None
      }
  val id = uid ?: ""
  val depth = roundDepth ?: 0 // TODO : Specify that this value hides the depth.
  return EliminationMatch(
      id = id,
      depth = depth,
      whiteName = whiteName,
      blackName = blackName,
      status = status,
  )
}
