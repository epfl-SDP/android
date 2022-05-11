package ch.epfl.sdp.mobile.test.application.tournaments

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.application.tournaments.toTournament
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class TournamentTest {

  @Test
  fun given_emptyTournamentDocument_when_transformedToTournament_then_returnsFilledTournament() {
    val document = TournamentDocument()
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns ""
    val tournament = document.toTournament(user)
    assertThat(tournament.reference).isEqualTo(TournamentReference(""))
    assertThat(tournament.name).isEmpty()
    assertThat(tournament.isAdmin).isFalse()
    assertThat(tournament.isParticipant).isFalse()
  }

  @Test
  fun given_filledTournamentDocument_when_transformedToTournament_then_returnsFilledTournament() {
    val document =
        TournamentDocument(uid = "1", name = "Hello", adminId = "id", playerIds = listOf("id"))
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "id"
    val tournament = document.toTournament(user)
    assertThat(tournament.reference).isEqualTo(TournamentReference("1"))
    assertThat(tournament.name).isEqualTo("Hello")
    assertThat(tournament.isAdmin).isTrue()
    assertThat(tournament.isParticipant).isTrue()
  }
}
