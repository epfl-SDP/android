package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.state.game.ActualArScreenState
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

  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val match = remember(chessFacade, id) { chessFacade.match(id) }

  val state = remember(match, scope) { ActualArScreenState(scope, match) }

  ArChessBoardScreen(state, modifier)
}
