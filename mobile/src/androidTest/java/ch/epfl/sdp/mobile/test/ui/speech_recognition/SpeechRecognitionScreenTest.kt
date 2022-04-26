package ch.epfl.sdp.mobile.test.ui.speech_recognition

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import ch.epfl.sdp.mobile.state.DefaultSpeechRecognitionScreenState
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.ui.speech_recognition.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import io.mockk.coEvery
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

  @Test
  fun given_listeningMic_when_talking_then_textDisplayed() {
    val mockedPermission = mockk<PermissionState>()
    every { mockedPermission.hasPermission } returns true

    val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(true))

    val speech = "Hello World"

    val mockedRecognizer: SpeechRecognizable = mockk()
    coEvery { mockedRecognizer.recognition(any()) } returns listOf(speech)

    rule.setContent { SpeechRecognitionScreen(state, recognizer = mockedRecognizer) }
    rule.onNodeWithText(PermissionGranted).assertExists()
    rule.onNodeWithText(DefaultText).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
    rule.onNodeWithText(speech).assertExists()
  }

  @get:Rule val androidRule = createAndroidComposeRule<HomeActivity>()

  @Test
  fun test() {

    val mockedPermission = mockk<PermissionState>()
    every { mockedPermission.hasPermission } returns true

    // Initiate Espresso intents listening
    try {
      Intents.init()

      // Rule sets content to test
      val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(true))
      androidRule.setContent { SpeechRecognitionScreen(state) }

      // Mock result intent
      val speech = "Hello World"
      val resultData =
          Intent(InstrumentationRegistry.getInstrumentation().context, HomeActivity::class.java)

      resultData.putExtra(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(speech))

      val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
      intending(hasAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)).respondWith(result)
      androidRule.onNodeWithText(DefaultText).assertExists()
      androidRule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()

      //TODO: not intents are captured by test
      // Possible bug -> recognizer keeps listening and do not send intents
      Log.d("tag", "all intents ${Intents.getIntents()}")
      androidRule.onNodeWithText(speech).assertExists(speech)
    } finally {
      Intents.release()
    }
  }
}
