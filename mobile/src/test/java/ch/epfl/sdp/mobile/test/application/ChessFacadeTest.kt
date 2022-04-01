package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.application.chess.engine.play
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChessFacadeTest {

  @Test
  fun match_hasRightId() {
    val facade = ChessFacade(emptyAuth(), emptyStore())
    val match = facade.match("id")

    assertThat(match.id).isEqualTo("id")
  }

  @Test
  fun missingMatch_hasEmptyProfiles() = runTest {
    val facade = ChessFacade(emptyAuth(), emptyStore())
    val match = facade.match("id")

    assertThat(match.black.first()).isNull()
    assertThat(match.white.first()).isNull()
  }

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
    val fetchedMatch = chessFacade.matches(user1).mapNotNull { it.firstOrNull() }.first()

    assertThat(createdMatch.white.filterNotNull().first().uid).isEqualTo(user1.uid)
    assertThat(createdMatch.black.filterNotNull().first().uid).isEqualTo(user2.uid)

    assertThat(fetchedMatch.white.filterNotNull().first().uid).isEqualTo(user1.uid)
    assertThat(fetchedMatch.black.filterNotNull().first().uid).isEqualTo(user2.uid)
  }

  @Test
  fun updatingAMatchWithNoId_doesNothing() = runTest {
    val auth = mockk<Auth>()
    val store = emptyStore()

    val chessFacade = ChessFacade(auth, store)
    // Player 1
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "userIdWhite"

    val match = Match()
    match.update(Game.create())
    val fetchedMatch = chessFacade.matches(user).map { it.firstOrNull() }.first()

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

    val match = chessFacade.matches(user).mapNotNull { it.firstOrNull() }.first()
    val newGame = Game.create().play { Position(0, 6) += Delta(0, -2) }

    match.update(newGame)

    val fetchedMatch = chessFacade.matches(user).mapNotNull { it.firstOrNull() }.first()

    assertThat(fetchedMatch.game.first().board[Position(0, 4)])
        .isEqualTo(newGame.board[Position(0, 4)])
  }
}
