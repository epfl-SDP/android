package ch.epfl.sdp.mobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

/** The root activity for the application, which is started when the user presses the app icon. */
class HomeActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { Example() }
  }
}
