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
  fun given_transactionDocument_when_settingAndGettingIt_then_itIsTheSame() = runTest {
    val reference = emptyStore().collection("tournaments").document("id")
    val tournamentDocument =
        TournamentDocument(
            adminId = "1",
            name = "The Grand Test Tournament",
            maxPlayers = 8,
            bestOf = 3,
            poolSize = 4,
            eliminationRounds = 1,
            playerIds = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
            pools =
                listOf(
                    PoolDocument(currentRound = 1, playerIds = listOf("1", "2", "3", "4")),
                    PoolDocument(currentRound = 2, playerIds = listOf("5", "6", "7", "8")),
                ),
        )

    reference.set(tournamentDocument)

    val expected = tournamentDocument.copy(uid = "id")
    val fetched = reference.asFlow<TournamentDocument>().filterNotNull().first()

    Truth.assertThat(fetched).isEqualTo(expected)
  }

  @Test
  fun given_emptyTransactionDocument_when_settingAndGettingIt_then_itIsTheSame() = runTest {
    val reference = emptyStore().collection("tournaments").document("id")
    val tournamentDocument = TournamentDocument()

    reference.set(tournamentDocument)

    val expected = tournamentDocument.copy(uid = "id")
    val fetched = reference.asFlow<TournamentDocument>().filterNotNull().first()

    Truth.assertThat(fetched).isEqualTo(expected)
  }

  @Test
  fun given_emptyTransactionEmptyPoolDocument_when_settingAndGettingIt_then_itIsTheSame() =
      runTest {
    val reference = emptyStore().collection("tournaments").document("id")
    val tournamentDocument = TournamentDocument(pools = listOf(PoolDocument()))

    reference.set(tournamentDocument)

    val expected = tournamentDocument.copy(uid = "id")
    val fetched = reference.asFlow<TournamentDocument>().filterNotNull().first()

    Truth.assertThat(fetched).isEqualTo(expected)
  }
}
