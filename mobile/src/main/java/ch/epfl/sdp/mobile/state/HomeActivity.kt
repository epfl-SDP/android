package ch.epfl.sdp.mobile.state

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.firebase.FirebaseAuth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreStore
import ch.epfl.sdp.mobile.ui.PawniesTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/** The root activity for the application, which is started when the user presses the app icon. */
class HomeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val auth = FirebaseAuth(Firebase.auth)
    val store = FirestoreStore(Firebase.firestore)

    // The different facades from the application.
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)

    setContent {
      PawniesTheme {
        ProvideLocalizedStrings {
          ProvideFacades(authentication = authenticationFacade, social = socialFacade) {
            Navigation()
          }
        }
      }
    }
  }
}
