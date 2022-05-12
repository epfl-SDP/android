package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store

interface Pool {
  val name: String
  val players: List<Player>
  data class Player(val uid: String, val name: String)
}

fun PoolDocument.toPool(user: AuthenticatedUser, store: Store): Pool = PoolDocumentPool(this, user)
