package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.SystemTimeProvider
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TournamentFacadeTest {

  @Test
  fun given_tournamentFacade_when_tournamentsExist_then_getTournamentsReturnsThemAll() {
    runTest {
      val auth = buildAuth { user("a@hotmail.com", "password", "1") }
      val dataStoreFactory = emptyDataStoreFactory()
      val store = buildStore {
        collection(TournamentDocument.Collection) {
          document("id1", TournamentDocument("id1", "1", "Tournament 1"))
        }
      }
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, SystemTimeProvider)
      val authenticationFacade = AuthenticationFacade(auth, store)
      val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val tournaments = tournamentFacade.tournaments(user).first()
      assertThat(tournaments[0].name).isEqualTo("Tournament 1")
      assertThat(tournaments[0].reference).isEqualTo(TournamentReference("id1"))
      assertThat(tournaments[0].isAdmin).isFalse()
      assertThat(tournaments[0].isParticipant).isFalse()
    }
  }

  @Test
  fun given_tournamentReference_when_joining_then_addsUserIdentifierInDocument() = runTest {
    val reference = TournamentReference(":-)")
    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(TournamentDocument.Collection) { document(reference.uid, TournamentDocument()) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, SystemTimeProvider)

    authFacade.signUpWithEmail("alexandre@example.org", "Alexandre", "passw0rd!")
    val user = authFacade.awaitAuthenticatedUser()

    tournamentFacade.join(user, reference)

    val document =
        store
            .collection(TournamentDocument.Collection)
            .document(reference.uid)
            .asFlow<TournamentDocument>()
            .filterNotNull()
            .first()
    assertThat(document.playerIds).containsExactly(user.uid)
  }

  @Test
  fun given_tournament_when_fetchingTournament_then_hasCorrectName() = runTest {
    val reference = TournamentReference(":-)")
    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(TournamentDocument.Collection) {
        document(reference.uid, TournamentDocument(name = "Hello there"))
      }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, SystemTimeProvider)

    authFacade.signUpWithEmail("alexandre@example.org", "Alexandre", "passw0rd!")
    val user = authFacade.awaitAuthenticatedUser()

    val tournament = tournamentFacade.tournament(reference, user).filterNotNull().first()

    assertThat(tournament.name).isEqualTo("Hello there")
  }

  @Test
  fun given_tournamentParameters_when_creatingTournament_then_fetchedTournamentHasSameParameters() =
      runTest {
    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, SystemTimeProvider)

    authFacade.signUpWithEmail("email@example.org", "user", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val ref =
        tournamentFacade.createTournament(
            user = user,
            name = "Test tournament",
            maxPlayers = 10,
            bestOf = 3,
            poolSize = 4,
            eliminationRounds = 2,
        )

    val fetched =
        store
            .collection(TournamentDocument.Collection)
            .document(ref!!.uid)
            .get<TournamentDocument>()

    assertThat(fetched?.adminId).isEqualTo(user.uid)
    assertThat(fetched?.name).isEqualTo("Test tournament")
    assertThat(fetched?.maxPlayers).isEqualTo(10)
    assertThat(fetched?.bestOf).isEqualTo(3)
    assertThat(fetched?.poolSize).isEqualTo(4)
    assertThat(fetched?.eliminationRounds).isEqualTo(2)
    assertThat(fetched?.playerIds).isNull()
  }

  @Test
  fun given_InvalidTournamentParameters_when_creatingTournament_then_returnsNull() = runTest {
    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, SystemTimeProvider)

    authFacade.signUpWithEmail("email@example.org", "user", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val ref =
        tournamentFacade.createTournament(
            user = user,
            name = "Test tournament",
            maxPlayers = 2,
            bestOf = 3,
            poolSize = 246,
            eliminationRounds = 1928,
        )

    assertThat(ref).isNull()
  }
}
