package ch.epfl.sdp.mobile.test.state

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers

/**
 * Executes the given [block] by returning an [ActivityResult] with the code
 * [Activity.RESULT_CANCELED] each time an intent is triggered. This simulates devices which do not
 * support AR.
 *
 * @param block the block of code to execute.
 */
inline fun withCanceledIntents(block: () -> Unit) {
  try {
    Intents.init()
    Intents.intending(IntentMatchers.anyIntent())
        .respondWith(ActivityResult(Activity.RESULT_CANCELED, null))
    block()
  } finally {
    Intents.release()
  }
}
