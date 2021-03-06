package ch.epfl.sdp.mobile.state

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.assets.android.AndroidAssetManager
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.firebase.FirebaseAuth
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXDataStoreFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreStore
import ch.epfl.sdp.mobile.infrastructure.sound.android.AndroidSoundPlayer
import ch.epfl.sdp.mobile.infrastructure.speech.android.AndroidSpeechRecognizerFactory
import ch.epfl.sdp.mobile.infrastructure.time.system.SystemTimeProvider
import ch.epfl.sdp.mobile.infrastructure.tts.android.AndroidTextToSpeechFactory
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
    val assetManager = AndroidAssetManager(context = this)
    val dataStoreFactory = AndroidXDataStoreFactory(context = this)

    // The different facades from the application.
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assetManager)
    val speechFacade =
        SpeechFacade(
            speechFactory = AndroidSpeechRecognizerFactory(this),
            textToSpeechFactory = AndroidTextToSpeechFactory(this),
            soundPlayer = AndroidSoundPlayer(this),
            dataStoreFactory = dataStoreFactory,
        )
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, SystemTimeProvider)
    val settingsFacade = SettingsFacade(dataStoreFactory)

    setContent {
      PawniesTheme {
        ProvideFacades(
            authentication = authenticationFacade,
            social = socialFacade,
            chess = chessFacade,
            speech = speechFacade,
            tournament = tournamentFacade,
            settings = settingsFacade,
        ) { ProvideLocalizedStrings { Navigation() } }
      }
    }
  }
}
