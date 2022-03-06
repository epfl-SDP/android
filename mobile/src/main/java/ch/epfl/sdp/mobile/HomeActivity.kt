package ch.epfl.sdp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import ch.epfl.sdp.mobile.data.api.firebase.FirebaseAuthenticationApi
import ch.epfl.sdp.mobile.data.features.authentication.AuthenticationApiAuthenticationScreenState
import ch.epfl.sdp.mobile.ui.features.authentication.AuthenticationScreen
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.ProvideLocalizedStrings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/** The root activity for the application, which is started when the user presses the app icon. */
class HomeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val api = FirebaseAuthenticationApi(Firebase.auth, Firebase.firestore)

    setContent {
      ProvideLocalizedStrings {
        val scope = rememberCoroutineScope()
        val strings = LocalLocalizedStrings.current
        val state =
            remember(api, strings, scope) {
              AuthenticationApiAuthenticationScreenState(api, strings, scope)
            }
        AuthenticationScreen(state)
      }
    }
  }
}
