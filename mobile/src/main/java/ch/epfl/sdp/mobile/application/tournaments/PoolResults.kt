package ch.epfl.sdp.mobile.application.tournaments

import androidx.compose.ui.util.fastSumBy
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.BlackWon
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.Stalemate
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.WhiteWon

/**
 * The [PoolResults] indicate the results of some pools, and provide aggregate statistics to rank
 * the players.
 */
interface PoolResults {

  /** The identifiers of the players who have some results. */
  val players: List<String>

  /** Computes the score of [player] against [opponent]. */
  fun against(player: String, opponent: String): Int

  /** Returns the total score of the player with [playerId]. */
  fun score(playerId: String): Int

  /** Returns the number of matches played by [playerId]. */
  fun played(playerId: String): Int
}

/**
 * An implementation of a [PoolResults] which use a list of [ChessDocument].
 *
 * @property documents the [ChessDocument] which are used to compute the results.
 */
private class ChessDocumentListPoolResults(
    private val documents: List<ChessDocument>,
) : PoolResults {
  override val players: List<String> =
      documents.flatMap { listOf(it.blackId, it.whiteId) }.distinct().filterNotNull()

  val scores =
      documents
          .flatMap {
            when (it.metadata?.status) {
              WhiteWon -> listOf(it.whiteId to 3)
              BlackWon -> listOf(it.blackId to 3)
              Stalemate -> listOf(it.whiteId to 1, it.blackId to 1)
              else -> emptyList()
            }
          }
          .groupingBy { (id, _) -> id }
          .fold(0) { acc, (_, score) -> acc + score }

  val played =
      documents
          .flatMap { listOf(it.whiteId to 1, it.blackId to 1) }
          .groupingBy { (id, _) -> id }
          .fold(0) { acc, (_, count) -> acc + count }

  override fun against(player: String, opponent: String): Int {
    return documents.fastSumBy {
      val status = it.metadata?.status
      when {
        status == WhiteWon && it.whiteId == player && it.blackId == opponent -> 3
        status == BlackWon && it.blackId == player && it.whiteId == opponent -> 3
        status == Stalemate &&
            ((it.blackId == player && it.whiteId == opponent) ||
                (it.whiteId == player && it.blackId == opponent)) -> 1
        else -> 0
      }
    }
  }

  override fun score(playerId: String): Int = scores[playerId] ?: 0

  override fun played(playerId: String): Int = played[playerId] ?: 0
}

/** Returns the [PoolResults] from a [List] off [ChessDocument]. */
fun List<ChessDocument>.toPoolResults(): PoolResults = ChessDocumentListPoolResults(this)
