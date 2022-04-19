package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.game.ar.ArChessBoardScreen

/**
 * A composable that make [ArChessBoardScreen] stateful
 *
 * @param id the identifier for the match.
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun StatefulArScreen(
    user: AuthenticatedUser,
    id: String,
    modifier: Modifier = Modifier,
) {

  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val match = remember(facade, id) { facade.match(id) }

  // FIXME : Not the cleanest code, but the faster way to deal with state with different UI. The AR
  //  Screen didn't need all methods in [SnapshotChessBoardState]. The cleaner way is to have a
  //  simpler snapshot that contains only information that will be needed by the
  //  [ArChessBoardScreen]
  val actions = StatefulGameScreenActions({}, {})

  val gameScreenState =
      remember(actions, user, match, scope) {
        SnapshotChessBoardState(
            actions = actions,
            user = user,
            match = match,
            scope = scope,
        )
      }

  ArChessBoardScreen(gameScreenState, modifier)
}
