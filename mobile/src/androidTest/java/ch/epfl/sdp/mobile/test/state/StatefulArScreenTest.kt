package ch.epfl.sdp.mobile.test.state

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulArScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.FakeTimeProvider
import org.junit.Rule

class StatefulArScreenTest {

  @get:Rule val permissionRule: GrantPermissionRule = grant(Manifest.permission.CAMERA)
  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  // Note(Chau) : The bug on CirrusCI (https://github.com/epfl-SDP/android/issues/213) seems to be
  // solve, I let this note here is case we encounter the bug again
  // Tested locally on device with and without ArCore installed
  // @Test
  fun given_allFacades_when_initStatefulArScreen_then_screenHasDescription() = withCanceledIntents {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection("users") { document("userId1", ProfileDocument()) }
      collection("games") {
        document("gameId", ChessDocument(whiteId = "userId1", blackId = "userId1"))
      }
    }

    val authApi = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournaments = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authApi, social, chess, speech, tournaments, settings) {
            StatefulArScreen("gameId")
          }
        }

    rule.onNodeWithContentDescription(strings.arContentDescription).assertExists()
  }
}
