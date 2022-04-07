package ch.epfl.sdp.mobile.test.state

import android.Manifest
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulSpeechRecognitionScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.ui.speech_recognition.DefaultText
import ch.epfl.sdp.mobile.ui.speech_recognition.ListeningText
import ch.epfl.sdp.mobile.ui.speech_recognition.MicroIconDescription
import ch.epfl.sdp.mobile.ui.speech_recognition.PermissionGranted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalPermissionsApi
class StatefulSpeechRecognitionScreenTest {

  @get:Rule val rule = createComposeRule()

  @get:Rule val permissionRule: GrantPermissionRule = grant(Manifest.permission.CAMERA)

  @Test
  fun given_defaultScreen_when_micClicked_and_okClicked_then_permissionGranted() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    rule.setContent {
      ProvideFacades(facade, social, chess) { StatefulSpeechRecognitionScreen(user) }
    }

    rule.onNodeWithText(PermissionGranted).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(ListeningText).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(DefaultText).assertExists()

  }
}
