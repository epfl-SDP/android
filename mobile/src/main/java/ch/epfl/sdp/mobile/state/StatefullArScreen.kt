package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.state.game.ActualChessBoardState
import ch.epfl.sdp.mobile.ui.game.ar.ArChessBoardScreen

/**
 * A composable that make [ArChessBoardScreen] stateful
 *
 * @param id the identifier for the match.
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun StatefulArScreen(
    id: String,
    modifier: Modifier = Modifier,
) {

  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val match = remember(facade, id) { facade.match(id) }

  val gameScreenState =
      remember(match, scope) {
        ActualChessBoardState(
            match = match,
            scope = scope,
        )
      }

  ArChessBoardScreen(gameScreenState, modifier)
}
