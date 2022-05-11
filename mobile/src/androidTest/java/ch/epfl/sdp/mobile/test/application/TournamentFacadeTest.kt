package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TournamentFacadeTest {

  @Test
  fun given_tournamentParameters_when_creatingTournament_then_fetchedTournamentHasSameParameters() =
      runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, store)

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

    val fetched = store.collection("tournaments").document(ref!!.uid).get<TournamentDocument>()

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
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val tournamentFacade = TournamentFacade(auth, store)

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
