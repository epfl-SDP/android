package ch.epfl.sdp.mobile.test.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.state.ProvideFacades as ActualProvideFacades
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides the required facades using the given [Auth] and [Store] instances, as well as some
 * coroutine dispatchers.
 *
 * @param auth the [Auth] used to create the facades.
 * @param store the [Store] used to create the facades.
 * @param ioDispatcher a [CoroutineDispatcher] used for I/O purposes. Defaults to [Dispatchers.Main]
 * for testing.
 * @param content the body of the composable, in which the facades will be provided.
 */
@Composable
fun ProvideTestFacades(
    auth: Auth,
    store: Store,
    ioDispatcher: CoroutineDispatcher = Dispatchers.Main,
    content: @Composable () -> Unit,
) {
  val authentication = remember(auth, store) { AuthenticationFacade(auth, store) }
  val chess = remember(auth, store, ioDispatcher) { ChessFacade(auth, store, ioDispatcher) }
  val social = remember(auth, store) { SocialFacade(auth, store) }
  ActualProvideFacades(
      authentication = authentication,
      chess = chess,
      social = social,
      content = content,
  )
}
