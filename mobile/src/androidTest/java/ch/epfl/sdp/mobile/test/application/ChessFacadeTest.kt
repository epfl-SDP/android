package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ChessMetadata
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.test.application.chess.engine.Games
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.test.application.chess.engine.play
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.FakeAssetManager
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
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
  fun match_hasRightId() {
    val facade = ChessFacade(emptyAuth(), emptyStore(), emptyAssets())
    val match = facade.match("id")

    assertThat(match.id).isEqualTo("id")
  }

  @Test
  fun missingMatch_hasEmptyProfiles() = runTest {
    val facade = ChessFacade(emptyAuth(), emptyStore(), emptyAssets())
    val match = facade.match("id")

    assertThat(match.black.first()).isNull()
    assertThat(match.white.first()).isNull()
  }

  @Test
  fun creatingAndFetchingAMatch_AreEquivalent() = runTest {
    val auth = mockk<Auth>()
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") {
        document("userId1", ProfileDocument(uid = "userId1"))
        document("userId2", ProfileDocument(uid = "userId2"))
      }
      collection("games") {}
    }

    val chessFacade = ChessFacade(auth, store, assets)
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
  fun updatingAMatchAndFetchingIt_IsConsistent() = runTest {
    val auth = mockk<Auth>()
    val assets = emptyAssets()
    val store = buildStore {
      collection("games") {
        document("gameId", ChessDocument(whiteId = "userId1", blackId = "userId2"))
      }
    }

    val chessFacade = ChessFacade(auth, store, assets)
    // Player 1
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "userId1"
    every { user.name } returns "userName1"

    val match = chessFacade.matches(user).mapNotNull { it.firstOrNull() }.first()
    val newGame = Game.create().play { Position(0, 6) += Delta(0, -2) }

    match.update(newGame)

    val fetchedMatch = chessFacade.matches(user).mapNotNull { it.firstOrNull() }.first()

    assertThat(fetchedMatch.game.first().board[Position(0, 4)])
        .isEqualTo(newGame.board[Position(0, 4)])
  }

  @Test
  fun given_aPuzzle_when_markingAsSolved_then_appearsInProfileSolvedPuzzles() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "009tE,6k1/6pp/p1N2p2/1pP2bP1/5P2/8/PPP5/3K4 b - - 1 28,f6g5 c6e7 g8f7 e7f5,600,103,90,340,crushing endgame fork short,https://lichess.org/fUV1iXBx/black#56\n",
        )

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    authFacade.signUpWithEmail("email@example.org", "user", "password")
    authFacade.signInWithEmail("email@example.org", "password")

    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val unsolvedPuzzles = chessFacade.unsolvedPuzzles(user)
    val solvedPuzzles = chessFacade.solvedPuzzles(user)

    assertThat(unsolvedPuzzles.size).isEqualTo(1)
    assertThat(solvedPuzzles).isEmpty()

    val puzzle = unsolvedPuzzles.first()
    assertThat(puzzle.uid).isEqualTo("009tE")
    user.solvePuzzle(puzzle)

    val newUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    assertThat(newUser.solvedPuzzles.size).isEqualTo(1)

    val newUnsolvedPuzzles = chessFacade.unsolvedPuzzles(newUser)
    val newSolvedPuzzles = chessFacade.solvedPuzzles(newUser)

    assertThat(newSolvedPuzzles.size).isEqualTo(1)
    assertThat(newUnsolvedPuzzles).isEmpty()
  }

  @Test
  fun given_userChangedName_when_aGameMoveHasBeenMade_then_whiteNameInMetadataShouldUpdate() =
      runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = buildStore {
      collection("games") {
        document(
            "gameId",
            ChessDocument(
                whiteId = "userId1",
                blackId = "userId2",
                metadata = ChessMetadata(null, "test1", "test2")))
      }
    }

    val chessFacade = ChessFacade(auth, store, assets)
    val authFacade = AuthenticationFacade(auth, store)

    authFacade.signUpWithEmail("email@example.org", "test3", "password")
    val userAuthenticated = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    store.collection("games").document("gameId").update { set("whiteId", userAuthenticated.uid) }

    val match = chessFacade.matches(userAuthenticated).mapNotNull { it.firstOrNull() }.first()
    val newGame = Game.create().play { Position(0, 6) += Delta(0, -2) }

    match.update(newGame)

    val chess = store.collection("games").document("gameId").get<ChessDocument>()

    assertThat(chess?.metadata?.whiteName).isEqualTo("test3")
  }

  @Test
  fun given_userChanngedName_when_aGameMovehasBeenMade_then_blackNameInMetadataShouldUpdate() =
      runTest {
    val assets = emptyAssets()
    val auth = emptyAuth()
    val store = buildStore {
      collection("games") {
        document(
            "gameId",
            ChessDocument(
                whiteId = "userId1",
                blackId = "userId2",
                metadata = ChessMetadata(null, "test1", "test2")))
      }
    }

    val chessFacade = ChessFacade(auth, store, assets)
    val authFacade = AuthenticationFacade(auth, store)

    authFacade.signUpWithEmail("email@example.org", "test3", "password")
    val userAuthenticated = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    store.collection("games").document("gameId").update { set("blackId", userAuthenticated.uid) }

    val match = chessFacade.matches(userAuthenticated).mapNotNull { it.firstOrNull() }.first()
    val newGame = Game.create().play { Position(0, 6) += Delta(0, -2) }

    match.update(newGame)

    val chess = store.collection("games").document("gameId").get<ChessDocument>()

    assertThat(chess?.metadata?.blackName).isEqualTo("test3")
  }

  @Test
  fun given_blackUserMakesFinalStep_when_gameIsNotDecidedYet_then_gameStateShouldShowBlackWins() =
      runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = buildStore {
      collection("games") {
        document(
            "gameId",
            ChessDocument(
                whiteId = "userId1",
                blackId = "test3",
                metadata = ChessMetadata(null, "test1", "test2")))
      }
    }

    val chessFacade = ChessFacade(auth, store, assets)
    val authFacade = AuthenticationFacade(auth, store)

    authFacade.signUpWithEmail("email@example.org", "test3", "password")
    val userAuthenticated = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    store.collection("games").document("gameId").update { set("blackId", userAuthenticated.uid) }

    val match = chessFacade.matches(userAuthenticated).mapNotNull { it.firstOrNull() }.first()
    val newGame = Game.create().play(FoolsMate)

    match.update(newGame)

    val chess = store.collection("games").document("gameId").get<ChessDocument>()

    assertThat(chess?.metadata?.status).isEqualTo("blackWon")
  }

  @Test
  fun given_blackUserMakesFinalStep_when_gameIsNotDecidedYet_then_gameStateShouldShowStalemate() =
      runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()

    val store = buildStore {
      collection("games") {
        document(
            "gameId",
            ChessDocument(
                whiteId = "test3",
                blackId = "userId1",
                metadata = ChessMetadata(null, "test1", "test2")))
      }
    }

    val chessFacade = ChessFacade(auth, store, assets)
    val authFacade = AuthenticationFacade(auth, store)

    authFacade.signUpWithEmail("email@example.org", "test3", "password")
    val userAuthenticated = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    store.collection("games").document("gameId").update { set("whiteId", userAuthenticated.uid) }

    val match = chessFacade.matches(userAuthenticated).mapNotNull { it.firstOrNull() }.first()
    val newGame = Game.create().play(Games.Stalemate)
    match.update(newGame)

    val chess = store.collection("games").document("gameId").get<ChessDocument>()

    assertThat(chess?.metadata?.status).isEqualTo("stalemate")
  }
}
