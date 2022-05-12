package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser

class PoolDocumentPool(
    private val document: PoolDocument,
    private val user: AuthenticatedUser,
) : Pool {

  override val name: String = document.name ?: ""

  override val remainingRounds: Int = document.remainingBestOfCount ?: 0
  override val totalRounds: Int = document.tournamentBestOf ?: 0

  override val isStartNextRoundEnabled: Boolean =
      user.uid == document.tournamentAdminId && remainingRounds > 0

  override val players: List<Pool.Player>
    get() {
      val ids = document.playerIds ?: emptyList()
      val names = document.playerNames ?: emptyList()
      return ids.zip(names).map { (id, name) -> Pool.Player(uid = id, name = name) }
    }
}
