package ch.epfl.sdp.mobile.test.application.chess.online

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Game
import ch.epfl.sdp.mobile.application.chess.online.ChessFacade
import ch.epfl.sdp.mobile.application.chess.online.Match
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChessFacadeTest {
  @Test
  fun creatingAndFetchingAMatch_AreEquivalent() = runTest {
    val auth = mockk<Auth>()
    val store = buildStore {
      collection("users") {
        document("userId1", ProfileDocument(uid = "userId1"))
        document("userId2", ProfileDocument(uid = "userId2"))
      }
      collection("games") {}
    }

    val chessFacade = ChessFacade(auth, store)
    // Player 1
    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    // Player 2
    val user2 = mockk<AuthenticatedUser>()
    every { user2.uid } returns "userId2"

    val createdMatch = chessFacade.createMatch(user1, user2)
    val fetchedMatch =
        chessFacade.fetchMatchesForUser(user1).mapNotNull { it.firstOrNull() }.first()

    assertThat(fetchedMatch.whiteId).isEqualTo(createdMatch.whiteId)
    assertThat(fetchedMatch.blackId).isEqualTo(createdMatch.blackId)
  }

  @Test
  fun updatingAMatchWithNoId_doesNothing() = runTest {
    val auth = mockk<Auth>()
    val store = emptyStore()

    val chessFacade = ChessFacade(auth, store)
    // Player 1
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "userIdWhite"

    val match = Match.create()
    chessFacade.updateMatch(match)
    val fetchedMatch = chessFacade.fetchMatchesForUser(user).map { it.firstOrNull() }.first()

    assertThat(fetchedMatch).isEqualTo(null)
  }

  @Test
  fun updatingAMatchAndFetchingIt_IsConsistent() = runTest {
    val auth = mockk<Auth>()
    val store = buildStore {
      collection("games") {
        document("gameId", ChessDocument(whiteId = "userId1", blackId = "userId2"))
      }
    }

    val chessFacade = ChessFacade(auth, store)
    // Player 1
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "userId1"

    val newMatch =
        Match(Game.create(), gameId = "gameId", whiteId = "userId1", blackId = "testIdBlack")
    chessFacade.updateMatch(newMatch)

    val fetchedMatch = chessFacade.fetchMatchesForUser(user).mapNotNull { it.firstOrNull() }.first()

    assertThat(fetchedMatch.blackId).isEqualTo(newMatch.blackId)
  }
}
