package ch.epfl.sdp.mobile.test.state

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulEditProfileImageDialogTest {

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_nameShouldBeUpdated() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument(name = "test", emoji = ":)")) }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade) { Navigation() }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNode(hasText("test") and hasSetTextAction()).performTextInput("2")
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText("test2").assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_cancelWithoutSave() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument("1", name = "test", emoji = ":)")) }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade) { Navigation() }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNodeWithText(strings.settingEditCancel).performClick()
    rule.onNodeWithText("test").assertIsDisplayed()
  }
}
