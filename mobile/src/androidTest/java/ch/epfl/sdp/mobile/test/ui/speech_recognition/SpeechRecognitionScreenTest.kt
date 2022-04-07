package ch.epfl.sdp.mobile.test.ui.speech_recognition

import android.Manifest
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import ch.epfl.sdp.mobile.state.DefaultSpeechRecognitionScreenState
import ch.epfl.sdp.mobile.ui.speech_recognition.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

@ExperimentalPermissionsApi
class SpeechRecognitionScreenTest {

  @get:Rule val rule = createComposeRule()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

  @Test
  fun given_defaultScreenState_when_showed_then_displayedDefaultScreen() {
    val mockedPermission = mockk<PermissionState>()
    every { mockedPermission.hasPermission } returns false
    val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(false))
    rule.setContent { SpeechRecognitionScreen(state) }
    // Permission is granted by default :(
    rule.onNodeWithText(PermissionDenied).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists()
    rule.onNodeWithText(DefaultText).assertExists()
  }

  @Test
  fun given_defaultScreenState_when_micClicked_then_ListeningDisplayed() {
    val mockedPermission = mockk<PermissionState>()
    every { mockedPermission.hasPermission } returns true
    val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(true))

    rule.setContent { SpeechRecognitionScreen(state) }
    rule.onNodeWithText(PermissionGranted).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(ListeningText).assertExists()
  }

  @Test
  fun given_listeningMic_when_micClicked_then_stopListening() {
    val mockedPermission = mockk<PermissionState>()
    every { mockedPermission.hasPermission } returns true
    val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(true))

    rule.setContent { SpeechRecognitionScreen(state) }
    rule.onNodeWithText(PermissionGranted).assertExists()
    rule.onNodeWithText(DefaultText).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(ListeningText).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()

    rule.onNodeWithText(ListeningText).assertDoesNotExist()
    rule.onNodeWithText(DefaultText).assertExists()
  }
}
