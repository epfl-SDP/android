package ch.epfl.sdp.mobile.androidTest.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.sharedTest.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.sharedTest.application.chess.engine.Games.Stalemate
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.sharedTest.ui.game.ChessBoardRobot
import ch.epfl.sdp.mobile.sharedTest.ui.game.click
import ch.epfl.sdp.mobile.sharedTest.ui.game.drag
import ch.epfl.sdp.mobile.sharedTest.ui.game.play
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulGameScreen
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulGameScreenTest {

  @get:Rule val rule = createComposeRule()

  /**
   * Returns a [ChessBoardRobot] with a store containing a player and an emptyGame with the player
   * playing against himself
   *
   * @param actions the [StatefulGameScreenActions] for this composable.
   */
  private fun emptyGameAgainstOneselfRobot(
      actions: StatefulGameScreenActions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
  ): ChessBoardRobot {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") {
        document("gameId", ChessDocument(whiteId = "userId1", blackId = "userId1"))
      }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess) { StatefulGameScreen(user1, "gameId", actions) }
        }

    return ChessBoardRobot(rule, strings)
  }

  @Test
  fun illegalMoveByClicking_isNotPossible() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput {
      click(4, 0)
      click(7, 7)
    }

    // White Rook is still here
    robot.assertHasPiece(7, 7, White, Rook)
    // Black King did not move
    robot.assertHasPiece(4, 0, Black, King)
  }

  @Test
  fun clickMovingPawns_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput {
      click(4, 6)
      click(4, 5)
    }
    robot.performInput {
      click(4, 1)
      click(4, 2)
    }

    robot.assertHasPiece(4, 5, White, Pawn)
    robot.assertHasPiece(4, 2, Black, Pawn)
  }

  @Test
  fun dragMovingPawns_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput { drag(ChessBoardState.Position(4, 6), ChessBoardState.Position(4, 5)) }
    robot.performInput { drag(ChessBoardState.Position(4, 1), ChessBoardState.Position(4, 2)) }

    robot.assertHasPiece(4, 5, White, Pawn)
    robot.assertHasPiece(4, 2, Black, Pawn)
  }

  @Test
  fun clickMovingRooks_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput {
      click(0, 6)
      click(0, 4)
    }
    robot.performInput {
      click(0, 1)
      click(0, 3)
    }

    // Move rooks
    robot.performInput {
      click(0, 7)
      click(0, 5)
    }
    robot.performInput {
      click(0, 0)
      click(0, 2)
    }

    robot.assertHasPiece(0, 5, White, Rook)
    robot.assertHasPiece(0, 2, Black, Rook)
  }

  @Test
  fun dragMovingRooks_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput { drag(ChessBoardState.Position(0, 6), ChessBoardState.Position(0, 4)) }
    robot.performInput { drag(ChessBoardState.Position(0, 1), ChessBoardState.Position(0, 3)) }

    // Move rooks
    robot.performInput { drag(ChessBoardState.Position(0, 7), ChessBoardState.Position(0, 5)) }
    robot.performInput { drag(ChessBoardState.Position(0, 0), ChessBoardState.Position(0, 2)) }

    robot.assertHasPiece(0, 5, White, Rook)
    robot.assertHasPiece(0, 2, Black, Rook)
  }

  @Test
  fun clickMovingKnights_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move knights
    robot.performInput {
      click(1, 7)
      click(2, 5)
    }
    robot.performInput {
      click(1, 0)
      click(2, 2)
    }

    robot.assertHasPiece(2, 5, White, Knight)
    robot.assertHasPiece(2, 2, Black, Knight)
  }

  @Test
  fun dragMovingKnights_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move knights
    robot.performInput { drag(ChessBoardState.Position(1, 7), ChessBoardState.Position(2, 5)) }
    robot.performInput { drag(ChessBoardState.Position(1, 0), ChessBoardState.Position(2, 2)) }

    robot.assertHasPiece(2, 5, White, Knight)
    robot.assertHasPiece(2, 2, Black, Knight)
  }

  @Test
  fun clickMovingBishops_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput {
      click(6, 6)
      click(6, 5)
    }
    robot.performInput {
      click(6, 1)
      click(6, 2)
    }

    // Move bishops
    robot.performInput {
      click(5, 7)
      click(7, 5)
    }
    robot.performInput {
      click(5, 0)
      click(7, 2)
    }

    robot.assertHasPiece(7, 5, White, Bishop)
    robot.assertHasPiece(7, 2, Black, Bishop)
  }

  @Test
  fun dragMovingBishops_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput { drag(ChessBoardState.Position(6, 6), ChessBoardState.Position(6, 5)) }
    robot.performInput { drag(ChessBoardState.Position(6, 1), ChessBoardState.Position(6, 2)) }

    // Move bishops
    robot.performInput { drag(ChessBoardState.Position(5, 7), ChessBoardState.Position(7, 5)) }
    robot.performInput { drag(ChessBoardState.Position(5, 0), ChessBoardState.Position(7, 2)) }

    robot.assertHasPiece(7, 5, White, Bishop)
    robot.assertHasPiece(7, 2, Black, Bishop)
  }

  @Test
  fun clickMovingQueens_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput {
      click(4, 6)
      click(4, 5)
    }
    robot.performInput {
      click(4, 1)
      click(4, 2)
    }

    // Move queens
    robot.performInput {
      click(3, 7)
      click(7, 3)
    }
    robot.performInput {
      click(3, 0)
      click(7, 4)
    }

    robot.assertHasPiece(7, 3, White, Queen)
    robot.assertHasPiece(7, 4, Black, Queen)
  }

  @Test
  fun dragMovingQueens_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput { drag(ChessBoardState.Position(4, 6), ChessBoardState.Position(4, 5)) }
    robot.performInput { drag(ChessBoardState.Position(4, 1), ChessBoardState.Position(4, 2)) }

    // Move queens
    robot.performInput { drag(ChessBoardState.Position(3, 7), ChessBoardState.Position(7, 3)) }
    robot.performInput { drag(ChessBoardState.Position(3, 0), ChessBoardState.Position(7, 4)) }

    robot.assertHasPiece(7, 3, White, Queen)
    robot.assertHasPiece(7, 4, Black, Queen)
  }

  @Test
  fun clickMovingKings_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput {
      click(4, 6)
      click(4, 5)
    }
    robot.performInput {
      click(4, 1)
      click(4, 2)
    }

    // Move kings
    robot.performInput {
      click(4, 7)
      click(4, 6)
    }
    robot.performInput {
      click(4, 0)
      click(4, 1)
    }

    robot.assertHasPiece(4, 6, White, King)
    robot.assertHasPiece(4, 1, Black, King)
  }

  @Test
  fun dragMovingKings_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput { drag(ChessBoardState.Position(4, 6), ChessBoardState.Position(4, 5)) }
    robot.performInput { drag(ChessBoardState.Position(4, 1), ChessBoardState.Position(4, 2)) }

    // Move kings
    robot.performInput { drag(ChessBoardState.Position(4, 7), ChessBoardState.Position(4, 6)) }
    robot.performInput { drag(ChessBoardState.Position(4, 0), ChessBoardState.Position(4, 1)) }

    robot.assertHasPiece(4, 6, White, King)
    robot.assertHasPiece(4, 1, Black, King)
  }

  @Test
  fun takingAQueen_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput {
      click(4, 6)
      click(4, 5)
    }
    robot.performInput {
      click(4, 1)
      click(4, 2)
    }

    // Move queens
    robot.performInput {
      click(3, 7)
      click(7, 3)
    }
    robot.performInput {
      click(3, 0)
      click(7, 4)
    }

    // White queen takes black queen
    robot.performInput {
      click(7, 3)
      click(7, 4)
    }

    robot.assertHasPiece(7, 4, White, Queen)
  }

  @Test
  fun castlingQueenSide_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move "knight" pawns out of the ways
    robot.performInput {
      click(1, 6)
      click(1, 5)
    }
    robot.performInput {
      click(1, 1)
      click(1, 2)
    }

    // Move "queen" pawns out of the ways
    robot.performInput {
      click(3, 6)
      click(3, 5)
    }
    robot.performInput {
      click(3, 1)
      click(3, 2)
    }

    // Move bishops
    robot.performInput {
      click(2, 7)
      click(0, 5)
    }
    robot.performInput {
      click(2, 0)
      click(0, 2)
    }

    // Move knights
    robot.performInput {
      click(1, 7)
      click(2, 5)
    }
    robot.performInput {
      click(1, 0)
      click(2, 2)
    }

    // Move queens
    robot.performInput {
      click(3, 7)
      click(3, 6)
    }
    robot.performInput {
      click(3, 0)
      click(3, 1)
    }

    // Castle queen-side
    robot.performInput {
      click(4, 7)
      click(2, 7)
    }
    robot.performInput {
      click(4, 0)
      click(2, 0)
    }

    robot.assertHasPiece(2, 7, White, King)
    robot.assertHasPiece(3, 7, White, Rook)
    robot.assertHasPiece(2, 0, Black, King)
    robot.assertHasPiece(3, 0, Black, Rook)
  }

  @Test
  fun castlingKingSide_isSuccessful() = runTest {
    val robot = emptyGameAgainstOneselfRobot()

    // Move pawns out of the ways
    robot.performInput {
      click(6, 6)
      click(6, 5)
    }
    robot.performInput {
      click(6, 1)
      click(6, 2)
    }

    // Move bishops
    robot.performInput {
      click(5, 7)
      click(7, 5)
    }
    robot.performInput {
      click(5, 0)
      click(7, 2)
    }

    // Move knights
    robot.performInput {
      click(6, 7)
      click(5, 5)
    }
    robot.performInput {
      click(6, 0)
      click(5, 2)
    }

    // Castle king-side
    robot.performInput {
      click(4, 7)
      click(6, 7)
    }
    robot.performInput {
      click(4, 0)
      click(6, 0)
    }

    robot.assertHasPiece(6, 7, White, King)
    robot.assertHasPiece(5, 7, White, Rook)
    robot.assertHasPiece(6, 0, Black, King)
    robot.assertHasPiece(5, 0, Black, Rook)
  }

  @Test
  fun selectingSameCellTwice_hasNoEffectOnBoard() {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput {
      click(4, 6)
      click(4, 6)
    }
    robot.assertHasPiece(4, 6, White, Pawn)
  }

  @Test
  fun selectingDifferentCells_movesPawn() {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput { click(4, 6) }
    rule.mainClock.advanceTimeByFrame() // Ensures we display the selected state.
    robot.performInput { click(4, 4) }
    robot.assertHasPiece(4, 4, White, Pawn)
  }

  @Test
  fun blockingCheck_isSuccessful() {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput {
      drag(ChessBoardState.Position(4, 6), ChessBoardState.Position(4, 5))
      drag(ChessBoardState.Position(5, 1), ChessBoardState.Position(5, 2))
      drag(ChessBoardState.Position(3, 7), ChessBoardState.Position(7, 3))
    }
    rule.mainClock.advanceTimeByFrame() // Ensures we display the check state.
    robot.performInput { drag(ChessBoardState.Position(6, 1), ChessBoardState.Position(6, 2)) }
    robot.assertHasPiece(6, 2, Black, Pawn)
  }

  @Test
  fun clickingOutOfBounds_doesNothing() {
    val robot = emptyGameAgainstOneselfRobot()

    robot.performInput {
      click(7, 7)
      click(7, 10)
    }

    // Rook did not move
    robot.assertHasPiece(7, 7, White, Rook)
  }

  @Test
  fun playingGameWithNoWhiteId_isUnsuccessful() {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") { document("gameId", ChessDocument(whiteId = null, blackId = "userId1")) }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val actions = StatefulGameScreenActions(onBack = {}, onShowAr = {})

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess) { StatefulGameScreen(user1, "gameId", actions) }
        }

    val robot = ChessBoardRobot(rule, strings)

    robot.performInput {
      click(4, 6)
      click(4, 5)
    }

    // Pawn did not move
    robot.assertHasPiece(4, 6, White, Pawn)
  }

  @Test
  fun playingGameWithNoBlackId_isUnsuccessful() {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") { document("gameId", ChessDocument(whiteId = "userId1", blackId = null)) }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val actions = StatefulGameScreenActions(onBack = {}, onShowAr = {})

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess) { StatefulGameScreen(user1, "gameId", actions) }
        }

    val robot = ChessBoardRobot(rule, strings)

    robot.performInput {
      click(4, 6)
      click(4, 5)
    }
    robot.performInput {
      click(4, 1)
      click(4, 2)
    }

    // Pawn did not move
    robot.assertHasPiece(4, 1, Black, Pawn)
  }

  @Test
  fun clickingListening_showsListeningText() {
    val robot = emptyGameAgainstOneselfRobot()

    robot.onNodeWithLocalizedContentDescription { gameMicOffContentDescription }.performClick()
    robot.onNodeWithLocalizedText { gameListening }.assertExists()
    robot.onNodeWithLocalizedContentDescription { gameMicOnContentDescription }.assertExists()
  }

  @Test
  fun clickingBack_callsBackAction() {
    var called = false
    val robot =
        emptyGameAgainstOneselfRobot(
            StatefulGameScreenActions(
                onBack = { called = true },
                onShowAr = {},
            ),
        )

    robot.onNodeWithLocalizedContentDescription { gameBack }.performClick()
    assertThat(called).isTrue()
  }

  @Test
  fun clickingAr_callsArAction() {
    var called = false
    val robot =
        emptyGameAgainstOneselfRobot(
            StatefulGameScreenActions(
                onBack = {},
                onShowAr = { called = true },
            ),
        )

    robot.onNodeWithLocalizedContentDescription { gameShowAr }.performClick()
    assertThat(called).isTrue()
  }

  @Test
  fun playingUntilCheckmate_displaysCheckmate() {
    val robot = emptyGameAgainstOneselfRobot()
    robot.play(FoolsMate)
    robot.onNodeWithLocalizedText { gameMessageCheckmate }.assertExists()
  }

  @Test
  fun playingUntilStalemate_displaysStalemate() {
    val robot = emptyGameAgainstOneselfRobot()
    robot.play(Stalemate)
    robot.onNodeWithLocalizedText { gameMessageStalemate }.assertExists()
  }
}
