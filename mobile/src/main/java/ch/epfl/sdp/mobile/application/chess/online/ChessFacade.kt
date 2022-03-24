package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.deserialize
import ch.epfl.sdp.mobile.application.serialize
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class ChessFacade(private val auth: Auth, private val store: Store) {

  suspend fun createMatch(white: Profile, black: Profile): Match {
    val match = Match.create(white.uid, black.uid)
    val chessDocument = match.serialize()
    store.collection("games").document().set(chessDocument)

    return match
  }

  suspend fun updateMatch(match: Match) {
    match.gameId ?: return
    val chessDocument = match.serialize()
    store.collection("games").document(match.gameId).set(chessDocument)
  }

  fun fetchMatch(matchId: String): Flow<Match> {
    return store.collection("games").document(matchId).asMatchFlow()
  }

  fun fetchMatchesForUser(profile: Profile): Flow<List<Match>> {
    val gamesAsWhite =
        queryGamesForPlayer(colorField = "whiteId", playerId = profile.uid).asMatchListFlow()
    val gamesAsBlack =
        queryGamesForPlayer(colorField = "blackId", playerId = profile.uid).asMatchListFlow()

    return merge(gamesAsWhite, gamesAsBlack)
  }

  fun fetchMatchesForPlayers(player: Profile, opponent: Profile): Flow<List<Match>> {
    val gamesAsWhite =
        queryGamesForPlayer(colorField = "whiteId", playerId = player.uid)
            .whereEquals("blackId", opponent.uid)
            .asMatchListFlow()

    val gamesAsBlack =
        queryGamesForPlayer(colorField = "blackId", playerId = player.uid)
            .whereEquals("whiteId", opponent.uid)
            .asMatchListFlow()

    return merge(gamesAsWhite, gamesAsBlack)
  }

  private fun queryGamesForPlayer(colorField: String, playerId: String): Query {
    return store.collection("games").whereEquals(colorField, playerId)
  }

  private fun Query.asMatchListFlow(): Flow<List<Match>> {
    return this.asFlow<ChessDocument>().map { it.map { doc -> doc.deserialize() } }
  }

  private fun DocumentReference.asMatchFlow(): Flow<Match> {
    return asFlow<ChessDocument>().map { it?.deserialize() ?: Match.create() }
  }
}
