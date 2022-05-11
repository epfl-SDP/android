@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.test.state

import android.Manifest.permission.RECORD_AUDIO
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.state.*
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toEngineRank
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.Stalemate
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.UntilPromotion
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.promote
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuccessfulSpeechRecognizer
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuccessfulSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuspendingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.ui.game.ChessBoardRobot
import ch.epfl.sdp.mobile.test.ui.game.click
import ch.epfl.sdp.mobile.test.ui.game.drag
import ch.epfl.sdp.mobile.test.ui.game.play
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
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
   * @param recognizer the [SpeechRecognizerFactory] used to make the speech request.
   * @param audioPermission the [PermissionState] to access audio.
   */
  private fun emptyGameAgainstOneselfRobot(
      actions: StatefulGameScreenActions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
      recognizer: SpeechRecognizerFactory = SuspendingSpeechRecognizerFactory,
      audioPermission: PermissionState = GrantedPermissionState,
  ): ChessBoardRobot {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") {
        document("gameId", ChessDocument(whiteId = "userId1", blackId = "userId1"))
      }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(recognizer)

    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess, speech) {
            StatefulGameScreen(user1, "gameId", actions, audioPermissionState = audioPermission)
          }
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
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") { document("gameId", ChessDocument(whiteId = null, blackId = "userId1")) }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val actions = StatefulGameScreenActions(onBack = {}, onShowAr = {})

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess, speech) {
            StatefulGameScreen(user1, "gameId", actions)
          }
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
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") { document("gameId", ChessDocument(whiteId = "userId1", blackId = null)) }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val actions = StatefulGameScreenActions(onBack = {}, onShowAr = {})

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess, speech) {
            StatefulGameScreen(user1, "gameId", actions)
          }
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

  @get:Rule val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(RECORD_AUDIO)

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

  @Test
  fun given_game_when_playingUntilPromotion_then_canPromoteToQueen() {
    val robot = emptyGameAgainstOneselfRobot()
    robot.play(promote(Rank.Queen))
    robot
        .onAllNodesWithContentDescription(
            robot.strings.boardPieceContentDescription(
                robot.strings.boardColorWhite,
                robot.strings.boardPieceQueen,
            ),
        )
        .assertCountEquals(2)
  }

  @Test
  fun given_rank_when_transformingToChessBoardStateRankAndBack_then_isEqual() {
    val ranks = listOf(*Rank.values())
    val mapped = ranks.map { it.toRank() }.map { it.toEngineRank() }
    assertThat(mapped).isEqualTo(ranks)
  }

  @Test
  fun given_promotionScreen_when_pressingRankTwice_then_confirmIsNotEnabled() {
    val robot = emptyGameAgainstOneselfRobot()
    robot.play(UntilPromotion)
    robot.performInput {
      drag(from = ChessBoardState.Position(7, 1), to = ChessBoardState.Position(6, 0))
    }
    robot.onNodeWithContentDescription(robot.strings.boardPieceQueen).performClick()
    robot.onNodeWithContentDescription(robot.strings.boardPieceQueen).performClick()
    robot.onNodeWithLocalizedText { robot.strings.gamePromoteConfirm }.assertIsNotEnabled()
  }

  @Test
  fun given_successfulRecognizer_when_clicksListening_then_displaysRecognitionResults() {
    // This will fail once we want to move the pieces instead.
    val robot =
        emptyGameAgainstOneselfRobot(
            recognizer = SuccessfulSpeechRecognizerFactory,
            audioPermission = GrantedPermissionState,
        )
    robot.onNodeWithLocalizedContentDescription { gameMicOffContentDescription }.performClick()
    robot.onNodeWithText(SuccessfulSpeechRecognizer.Results[0]).assertExists()
  }

  @Test
  fun given_failingRecognizer_when_clicksListening_then_displaysFailedRecognitionResults() {
    // This will fail once we want to move the pieces instead.
    val robot =
        emptyGameAgainstOneselfRobot(
            recognizer = FailingSpeechRecognizerFactory,
            audioPermission = GrantedPermissionState,
        )
    robot.onNodeWithLocalizedContentDescription { gameMicOffContentDescription }.performClick()
    robot.onNodeWithText("Internal failure").assertExists()
  }

  @Test
  fun given_noPermission_when_clicksListening_then_requestsPermission() {
    val permission = MissingPermissionState()
    val robot =
        emptyGameAgainstOneselfRobot(
            recognizer = SuspendingSpeechRecognizerFactory,
            audioPermission = permission,
        )
    robot.onNodeWithLocalizedContentDescription { gameMicOffContentDescription }.performClick()
    assertThat(permission.permissionRequested).isTrue()
  }

  @Test
  fun given_suspendingRecognizer_when_clickingListeningTwice_then_cancelsRecognition() {
    val robot =
        emptyGameAgainstOneselfRobot(
            recognizer = SuspendingSpeechRecognizerFactory,
            audioPermission = GrantedPermissionState,
        )
    robot.onNodeWithLocalizedContentDescription { gameMicOffContentDescription }.performClick()
    robot.onNodeWithLocalizedText { gameListening }.performClick()
    robot.onNodeWithLocalizedContentDescription { gameMicOffContentDescription }.assertExists()
  }
}

object GrantedPermissionState : PermissionState {
  override val hasPermission = true
  override val permission = RECORD_AUDIO
  override val permissionRequested = true
  override val shouldShowRationale = false
  override fun launchPermissionRequest() = Unit
}

class MissingPermissionState : PermissionState {
  override var permissionRequested by mutableStateOf(false)
  override val permission = RECORD_AUDIO
  override val hasPermission
    get() = permissionRequested
  override val shouldShowRationale = false
  override fun launchPermissionRequest() {
    permissionRequested = true
  }
}
