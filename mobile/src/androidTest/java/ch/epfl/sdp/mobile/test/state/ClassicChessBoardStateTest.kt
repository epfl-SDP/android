package ch.epfl.sdp.mobile.test.state

import androidx.compose.material.SnackbarHostState
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.ActualGameScreenState
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.UnknownCommandSpeechRecognizerFactory
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ClassicChessBoardStateTest {

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun selectingPiece_displaysAvailableMoves() = runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("id1", ProfileDocument())
        document("id2", ProfileDocument())
      }
      collection(ChessDocument.Collection) {
        document("id", ChessDocument(whiteId = "id1", blackId = "id2"))
      }
    }
    val facade = ChessFacade(auth, store, assets)
    val speechFacade = SpeechFacade(UnknownCommandSpeechRecognizerFactory)
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "id1"

    val job = Job()
    val scope = CoroutineScope(job)

    val match = facade.createMatch(user, user)

    val actions = StatefulGameScreenActions(onBack = {}, onShowAr = {})
    val state =
        ActualGameScreenState(
            actions = actions,
            user = user,
            match = match,
            permission = GrantedPermissionState,
            speechFacade = speechFacade,
            snackbarHostState = SnackbarHostState(),
            scope = scope,
        )

    state.onPositionClick(ChessBoardState.Position(4, 6))
    assertThat(state.availableMoves)
        .containsExactly(
            ChessBoardState.Position(4, 5),
            ChessBoardState.Position(4, 4),
        )

    job.cancel()
  }
}
