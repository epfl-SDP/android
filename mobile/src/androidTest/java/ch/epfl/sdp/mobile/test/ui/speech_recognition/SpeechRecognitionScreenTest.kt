package ch.epfl.sdp.mobile.test.ui.speech_recognition

import android.Manifest
import android.os.Bundle
import android.speech.RecognitionListener
import android.util.Log
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
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Rule
import org.junit.Test

@ExperimentalPermissionsApi
class SpeechRecognitionScreenTest {

  @get:Rule val rule = createComposeRule()

  @Test fun given_noGrantedPermission_when_screenDisplayed_then_noPermissionText(){
    val mockedPermission = mockk<PermissionState>()
    every { mockedPermission.hasPermission } returns false
    val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(false))
    rule.setContent { SpeechRecognitionScreen(state) }

    rule.onNodeWithText(PermissionDenied).assertExists()
    rule.onNodeWithContentDescription(MicroIconDescription).assertExists()
    rule.onNodeWithText(DefaultText).assertExists()
  }

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

  //Start activity
//  @get:Rule val androidRule = createAndroidComposeRule<HomeActivity>()
//
//  @Test
//  fun brokenIntentsTest() {
//
//    val mockedPermission = mockk<PermissionState>()
//    every { mockedPermission.hasPermission } returns true
//
//    try {
//      //Initiate intent capturing
//      Intents.init()
//
//      // Rule sets content to test
//      val state = DefaultSpeechRecognitionScreenState(mockedPermission, mutableStateOf(true))
//      androidRule.setContent{ SpeechRecognitionScreen(state) }
//
//
//      // Mock result intent
//      val speech = "Hello World"
//      val resultData = Intent(androidRule.activity.applicationContext, HomeActivity::class.java)
//      resultData.putExtra(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(speech))
//
//      val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//      //Stub intent responses of the recognizer
//      intending(hasAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)).respondWith(result)
//
//      androidRule.onNodeWithText(DefaultText).assertExists()
//
//      //Start listen for result but never stop
//      androidRule.onNodeWithContentDescription(MicroIconDescription).assertExists().performClick()
//    // androidRule.onNodeWithContentDescription(ListeningText).assertExists()
//
//      // TODO: no intents are captured by test
//      // Possible bug -> recognizer keeps listening and do not send intents
//
//      Log.d("tag", "all intents ${Intents.getIntents()}") // No intents are sent by the HomeActivity Class
//      // The recognizer keeps listening
//
//      //This fails as no response is sent back to the HomeActivity
//      androidRule.onNodeWithText(speech).assertExists(speech)
//    } finally {
//      //Stop intent capturing
//      Intents.release()
//    }
//  }

  @Test fun testListener() = runTest{
    val testResults = arrayListOf("Hello", "World")
    val listenerResults = suspendCancellableCoroutine<List<String>> { cont ->
      val bundle : Bundle = mockk()
      every { bundle.getStringArrayList(any())} returns testResults
      defaultListener(cont).onResults(bundle)
      cont.invokeOnCancellation {
        Log.d("TAGG", "Canceling...")
        assertThat(1, IsEqual(1))
      }
      cont.cancel()
    }

    assertThat(listenerResults,IsEqual(testResults))




  }

  @Test fun testListenerEmpty() = runTest {
    val listenerResults = suspendCancellableCoroutine<List<String>> { cont ->
      val bundle : Bundle = mockk()
      every {bundle.getStringArrayList(any())}  returns null
      defaultListener(cont).onResults(bundle)
    }

    Log.d("TAG", "After")
    assertThat(listenerResults, IsEqual(emptyList()))

  }
}
