package ch.epfl.sdp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** The root activity for the application, which is started when the user presses the app icon. */
class HomeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { Example() }
  }
}
