package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import com.google.common.truth.Truth.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TournamentFacadeTest {

  @Test
  fun given_tournamentFacade_when_tournamentsExist_then_getTournamentsReturnsThemAll() {
    runTest {
      val auth = buildAuth { user("a@hotmail.com", "password", "1") }
      val store = buildStore {
        collection("tournaments") {
          document("id1", TournamentDocument("id1", "1", "Tournament 1"))
        }
      }
      val tournamentFacade = TournamentFacade(auth, store)
      val authenticationFacade = AuthenticationFacade(auth, store)
      val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val tournaments = tournamentFacade.tournaments(user).first()
      assertThat(tournaments[0].name).isEqualTo("Tournament 1")
      assertThat(tournaments[0].reference).isEqualTo(TournamentReference("id1"))
      assertThat(tournaments[0].isAdmin).isFalse()
      assertThat(tournaments[0].isParticipant).isFalse()
    }
  }
}
