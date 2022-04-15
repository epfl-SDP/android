package ch.epfl.sdp.mobile.test.ui.game.ar

import android.Manifest
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import ch.epfl.sdp.mobile.state.HomeActivity
import org.junit.Rule

class ArScreenKtTest {

  @get:Rule val permissionRule: GrantPermissionRule = grant(Manifest.permission.CAMERA)
  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  // FIXME : This is currently broken on CirrusCI (https://github.com/epfl-SDP/android/issues/213)
  // @Test
  /*  fun check_thatComposableHasTheTag() = withCanceledIntents {
    val game = Game.create()
    val strings = rule.setContentWithLocalizedStrings { ArScreen(game) }
    rule.onNodeWithContentDescription(strings.arContentDescription).assertExists()
  }*/

  /**
   * Executes the given [block] by returning an [ActivityResult] with the code
   * [Activity.RESULT_CANCELED] each time an intent is triggered. This simulates devices which do
   * not support AR.
   *
   * @param block the block of code to execute.
   */
  private inline fun withCanceledIntents(block: () -> Unit) {
    try {
      init()
      intending(anyIntent()).respondWith(ActivityResult(Activity.RESULT_CANCELED, null))
      block()
    } finally {
      release()
    }
  }
}
