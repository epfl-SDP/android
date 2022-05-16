package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class StoreDocumentsTest {

  @Test
  fun given_tournamentDocuments_when_settingAndGettingThem_then_theyAreTheSame() = runTest {
    val tournamentId = "tournamentId"
    val store = emptyStore()
    val tournaments = store.collection(TournamentDocument.Collection)
    val pools = store.collection("tournaments/$tournamentId")

    val tournament = tournaments.document(tournamentId)
    val pool1 = pools.document("pool1")
    val pool2 = pools.document("pool2")

    val tournamentDocument =
        TournamentDocument(
            adminId = "1",
            name = "The Grand Test Tournament",
            maxPlayers = 8,
            bestOf = 3,
            poolSize = 4,
            eliminationRounds = 1,
            playerIds = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
        )
    val poolDocument1 =
        PoolDocument(
            tournamentId = tournamentId,
            playerIds = listOf("1", "2", "3", "4"),
        )
    val poolDocument2 =
        PoolDocument(
            tournamentId = tournamentId,
            playerIds = listOf("5", "6", "7", "8"),
        )

    tournament.set(tournamentDocument)
    pool1.set(poolDocument1)
    pool2.set(poolDocument2)

    val expectedTournament = tournamentDocument.copy(uid = tournamentId)
    val expectedPool1 = poolDocument1.copy(uid = "pool1")
    val expectedPool2 = poolDocument2.copy(uid = "pool2")

    val fetchedTournament = tournament.asFlow<TournamentDocument>().filterNotNull().first()
    val fetchedPools =
        pools
            .whereEquals("tournamentId", tournamentId)
            .asFlow<PoolDocument>()
            .filterNotNull()
            .first()

    Truth.assertThat(fetchedTournament).isEqualTo(expectedTournament)
    Truth.assertThat(fetchedPools).containsExactly(expectedPool1, expectedPool2)
  }

  @Test
  fun given_emptyTournamentDocuments_when_settingAndGettingThem_then_theyAreTheSame() = runTest {
    val tournamentId = "tournamentId"
    val store = emptyStore()
    val tournaments = store.collection(TournamentDocument.Collection)
    val pools = store.collection("tournaments/$tournamentId")

    val tournament = tournaments.document(tournamentId)
    val pool = pools.document("poolId")

    val tournamentDocument = TournamentDocument()
    val poolDocument = PoolDocument()

    tournament.set(tournamentDocument)
    pool.set(poolDocument)

    val expectedTournament = tournamentDocument.copy(uid = tournamentId)
    val expectedPool1 = poolDocument.copy(uid = "poolId")

    val fetchedTournament = tournament.asFlow<TournamentDocument>().filterNotNull().first()
    val fetchedPool1 = pool.asFlow<PoolDocument>().filterNotNull().first()

    Truth.assertThat(fetchedTournament).isEqualTo(expectedTournament)
    Truth.assertThat(fetchedPool1).isEqualTo(expectedPool1)
  }
}
