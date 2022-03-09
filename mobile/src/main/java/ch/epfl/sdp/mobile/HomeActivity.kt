package ch.epfl.sdp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.RectangleShape
import ch.epfl.sdp.mobile.backend.store.firestore.FirestoreStore
import ch.epfl.sdp.mobile.data.api.firebase.FirebaseAuthenticationApi
import ch.epfl.sdp.mobile.ui.ProvideApis
import ch.epfl.sdp.mobile.ui.branding.PawniesTheme
import ch.epfl.sdp.mobile.ui.features.Navigation
import ch.epfl.sdp.mobile.ui.i18n.ProvideLocalizedStrings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/** The root activity for the application, which is started when the user presses the app icon. */
class HomeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val authentication = FirebaseAuthenticationApi(Firebase.auth, FirestoreStore(Firebase.firestore))

    RectangleShape

    RectangleShape

    setContent {
      PawniesTheme {
        ProvideLocalizedStrings {
          ProvideApis(
              authentication = authentication,
          ) { Navigation() }
        }
      }
    }
  }
}
