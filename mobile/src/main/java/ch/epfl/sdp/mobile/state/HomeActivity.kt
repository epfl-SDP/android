package ch.epfl.sdp.mobile.state

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.epfl.sdp.mobile.application.firebase.FirebaseAuthenticationFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreStore
import ch.epfl.sdp.mobile.ui.PawniesTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/** The root activity for the application, which is started when the user presses the app icon. */
class HomeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val authentication =
        FirebaseAuthenticationFacade(Firebase.auth, FirestoreStore(Firebase.firestore))

    setContent {
      PawniesTheme {
        ProvideLocalizedStrings {
          ProvideFacades(
              authentication = authentication,
          ) { Navigation() }
        }
      }
    }
  }
}
