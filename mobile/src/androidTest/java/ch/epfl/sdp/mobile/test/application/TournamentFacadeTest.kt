package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TournamentFacadeTest {

  @Test
  fun given_tournamentReference_when_joining_then_addsUserIdentifierInDocument() = runTest {
    val reference = TournamentReference(":-)")
    val auth = emptyAuth()
    val store = buildStore {
      collection(TournamentDocument.Collection) { document(reference.uid, TournamentDocument()) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, store)

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
    val store = buildStore {
      collection(TournamentDocument.Collection) {
        document(reference.uid, TournamentDocument(name = "Hello there"))
      }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("alexandre@example.org", "Alexandre", "passw0rd!")
    val user = authFacade.awaitAuthenticatedUser()

    val tournament = tournamentFacade.tournament(reference, user).filterNotNull().first()

    assertThat(tournament.name).isEqualTo("Hello there")
  }
}
