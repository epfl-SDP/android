package ch.epfl.sdp.mobile.test.state.game.delegating

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingArState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DelegatingArStateTest {

  @Test
  fun given_aNewGame_when_createARDelegation_then_correctListOfPieces() = runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("id1", ProfileDocument()) }
      collection("games") { document("id", ChessDocument(whiteId = "id1", blackId = "id2")) }
    }
    val facade = ChessFacade(auth, store, assets)
    val user = mockk<AuthenticatedUser>()
    every { user.uid } returns "id1"

    val job = Job()
    val scope = CoroutineScope(job)

    val match = facade.createMatch(user, user)

    val delegate = DelegatingArState(match, scope)

    val expectedPieces =
        Game.create().board.associate { (pos, piece) ->
          pos.toPosition() to DelegatingChessBoardState.Piece(piece)
        }

    assertThat(delegate.pieces).isEqualTo(expectedPieces)
  }
}
