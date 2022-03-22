package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.*
import ch.epfl.sdp.mobile.application.chess.Game
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChessFacade(private val auth: Auth, private val store: Store) {

  suspend fun updateGame(id: String, game: Game) {
    val reference = store.collection("games").document(id)
    val chessDocument = game.serialize()
    reference.set(chessDocument)
  }

  fun game(id: String): Flow<Game> {
    return store.collection("games").document(id).asGameFlow()
  }

  private fun DocumentReference.asGameFlow(): Flow<Game> {
    return asFlow<ChessDocument>().map { it }.map { it?.deserialize() ?: Game.create() }
  }
}
