package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.online.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulGameScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.ui.game.ChessBoardRobot
import ch.epfl.sdp.mobile.test.ui.game.drag
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.Pawn
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulGameScreenTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun movingTwoPawns_isSuccessful() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") {
        document("userId1", ProfileDocument(uid = "userId1"))
        document("userId2", ProfileDocument(uid = "userId2"))
      }
      collection("games") {
        document(
            "gameId",
            ChessDocument(uid = "uid", moves = null, whiteId = "userId1", blackId = "userId2"))
      }
    }
    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    // Player 1
    val user1 = mockk<AuthenticatedUser>()
    every { user1.uid } returns "userId1"

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess) { StatefulGameScreen(user1) }
        }

    val robot = ChessBoardRobot(rule, strings)

    robot.performInput { drag(ChessBoardState.Position(0, 6), ChessBoardState.Position(0, 4)) }
    robot.performInput { drag(ChessBoardState.Position(0, 1), ChessBoardState.Position(0, 3)) }

    robot.assertHasPiece(0, 4, White, Pawn)
    robot.assertHasPiece(0, 3, Black, Pawn)
  }
}
