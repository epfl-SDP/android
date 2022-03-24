package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.deserialize
import ch.epfl.sdp.mobile.application.serialize
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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
    val gamesAsWhite = getMatchesForPlayer(colorField = "whiteId", playerId = profile.uid)
    val gamesAsBlack = getMatchesForPlayer(colorField = "blackId", playerId = profile.uid)

    return combine(gamesAsWhite, gamesAsBlack) { (a, b) -> a + b }
  }

  fun fetchMatchesForPlayers(player: Profile, opponent: Profile): Flow<List<Match>> {
    val gamesAsWhite = getMatchesForPlayers(player.uid, opponent.uid)

    val gamesAsBlack = getMatchesForPlayers(opponent.uid, player.uid)

    return combine(gamesAsWhite, gamesAsBlack) { (a, b) -> a + b }
  }

  private fun getMatchesForPlayer(colorField: String, playerId: String): Flow<List<Match>> {
    return store.collection("games").whereEquals(colorField, playerId).asMatchListFlow().onStart {
      emit(emptyList())
    }
  }

  private fun getMatchesForPlayers(
      whitePlayerId: String,
      blackPlayerId: String
  ): Flow<List<Match>> {
    return store
        .collection("games")
        .whereEquals("whiteId", whitePlayerId)
        .whereEquals("blackId", blackPlayerId)
        .asMatchListFlow()
        .onStart { emit(emptyList()) }
  }

  private fun Query.asMatchListFlow(): Flow<List<Match>> {
    return this.asFlow<ChessDocument>().map { it.map { doc -> doc.deserialize() } }
  }

  private fun DocumentReference.asMatchFlow(): Flow<Match> {
    return asFlow<ChessDocument>().map { it?.deserialize() ?: Match.create() }
  }
}
