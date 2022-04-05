package ch.epfl.sdp.mobile.test.ui.speech_recognition

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.ui.speech_recognition.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.junit.Rule
import org.junit.Test

private const val GrantText = "ALLOW"
private const val DenyText = "DENY"

@ExperimentalPermissionsApi
class SpeechRecognitionScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_defaultScreenState_when_showed_then_displayedDefaultScreen() {
    val state =
        object : SpeechRecognitionScreenState {
          override val microphonePermissionState: MutableState<Boolean> = mutableStateOf(false)
          override val onPermissionChange: () -> Unit = {
            microphonePermissionState.value = !microphonePermissionState.value
          }
        }

    rule.setContent { SpeechRecognitionScreen(state) }
    rule.onNodeWithText(PermissionDenied).assertDoesNotExist()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists()
    rule.onNodeWithText(DefaultText).assertExists()
  }

  @Test
  fun given_defaultScreenState_when_micClicked_then_displayPermissionDialog() {
    val state =
        object : SpeechRecognitionScreenState {
          override val microphonePermissionState: MutableState<Boolean> = mutableStateOf(false)
          override val onPermissionChange: () -> Unit = {
            microphonePermissionState.value = !microphonePermissionState.value
          }
        }

    rule.setContent { SpeechRecognitionScreen(state) }
    rule.onNodeWithText(PermissionDenied).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(GrantText).assertExists()
  }

  @Test
  fun given_permissionDialog_when_okClicked_then_grantPermission() {
    val state =
        object : SpeechRecognitionScreenState {
          override val microphonePermissionState: MutableState<Boolean> = mutableStateOf(false)
          override val onPermissionChange: () -> Unit = {
            microphonePermissionState.value = !microphonePermissionState.value
          }
        }

    rule.setContent { SpeechRecognitionScreen(state) }
    rule.onNodeWithText(PermissionDenied).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(GrantText).assertExists().performClick()
    rule.onNodeWithText(PermissionGranted).assertExists()
  }
}
