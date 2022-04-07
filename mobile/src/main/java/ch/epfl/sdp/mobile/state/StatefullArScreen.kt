package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.ui.ar.ArScreen

@Composable
fun StatefulArScreen(
    modifier: Modifier = Modifier,
) {

  // FIX ME : Take the game for a give id
  val game = Game.create()

  ArScreen(game, modifier)
}
