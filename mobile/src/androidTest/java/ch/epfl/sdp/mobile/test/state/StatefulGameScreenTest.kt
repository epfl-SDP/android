package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.*
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
import com.google.common.truth.Truth.assertThat
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

    val boardNode =
        rule.onNodeWithContentDescription(strings.boardContentDescription).fetchSemanticsNode()

    val density = requireNotNull(boardNode.root).density
    val size = boardNode.size
    val boardBounds = boardNode.boundsInRoot
    assertThat(size.height).isEqualTo(size.width)

    val squareSize = size.width / 8
    val squareSizeDp = with(density) { size.width.toDp() } / 8

    // Move white pawn
    rule.onNodeWithContentDescription(strings.boardContentDescription).performTouchInput {
      down(pos(0, 6, density, squareSizeDp))
      moveTo(pos(0, 4, density, squareSizeDp))
      up()
    }

    // Move black pawn
    rule.onNodeWithContentDescription(strings.boardContentDescription).performTouchInput {
      down(pos(0, 1, density, squareSizeDp))
      moveTo(pos(0, 3, density, squareSizeDp))
      up()
    }

    // Check white pawn position
    rule.onAllNodesWithContentDescription(
            strings.boardPieceContentDescription(strings.boardColorWhite, strings.boardPiecePawn),
        )
        .assertAny(
            SemanticsMatcher("InBounds") {
              val (x, y) = it.boundsInRoot.center
              x in boardBounds.left..boardBounds.left + squareSize
              y in (boardBounds.top + squareSize * 4)..(boardBounds.top + squareSize * 5)
            },
        )

    // Check black pawn position
    rule.onAllNodesWithContentDescription(
            strings.boardPieceContentDescription(strings.boardColorBlack, strings.boardPiecePawn),
        )
        .assertAny(
            SemanticsMatcher("InBounds") {
              val (x, y) = it.boundsInRoot.center
              x in boardBounds.left..boardBounds.left + squareSize
              y in (boardBounds.top + squareSize * 3)..(boardBounds.top + squareSize * 4)
            },
        )
  }

  private fun pos(x: Int, y: Int, density: Density, squareSize: Dp): Offset {
    with(density) {
      return Offset(x.toAbsDp(squareSize).toPx(), y.toAbsDp(squareSize).toPx())
    }
  }

  private fun Int.toAbsDp(squareSizeDp: Dp): Dp {
    return (squareSizeDp / 2) + squareSizeDp.times(this)
  }
}
