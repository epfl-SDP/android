package ch.epfl.sdp.mobile.application.tournaments

import androidx.compose.ui.util.fastSumBy
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.BlackWon
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.Stalemate
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.WhiteWon

interface PoolResults {
  fun against(player: String, opponent: String): Int
  fun score(playerId: String): Int
}

fun List<ChessDocument>.toPoolResults(): PoolResults =
    object : PoolResults {

      val scores =
          this@toPoolResults.flatMap {
            when (it.metadata?.status) {
              WhiteWon -> listOf(it.whiteId to 3)
              BlackWon -> listOf(it.blackId to 3)
              Stalemate -> listOf(it.whiteId to 1, it.blackId to 1)
              else -> emptyList()
            }
          }
              .groupingBy { (id, _) -> id }
              .fold(0) { acc, (_, score) -> acc + score }

      override fun against(player: String, opponent: String): Int {
        return this@toPoolResults.fastSumBy {
          val status = it.metadata?.status
          when {
            status == WhiteWon && it.whiteId == player && it.blackId == opponent -> 3
            status == BlackWon && it.blackId == player && it.whiteId == opponent -> 3
            status == Stalemate && (it.blackId == player || it.whiteId == player) -> 1
            else -> 0
          }
        }
      }

      override fun score(playerId: String): Int = scores[playerId] ?: 0
    }
