package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulPlayScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPlayScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_playScreen_when_clickingNewGame_then_localAndOnlinePlayAreDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(
                user = currentUser,
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayLocal).assertExists()
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists()
  }

  @Test
  fun given_playScreen_when_clickingNewGameAndLocalPlay_then_localGameCallbackIsCalled() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(
                user = currentUser,
                navigateToPrepareGame = {},
                navigateToLocalGame = {
                  channel.trySend(Unit)
                  channel.close()
                },
            )
          }
        }

    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayLocal).performClick()

    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_playScreen_when_clickingNewGameAndOnlinePlay_then_onlineGameCallbackIsCalled() =
      runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(
                user = currentUser,
                navigateToPrepareGame = {
                  channel.trySend(Unit)
                  channel.close()
                },
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayOnline).performClick()

    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }
}
