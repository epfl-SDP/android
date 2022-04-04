package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * Composable that implements a complete PrepareGame screen
 * @param state current state of the screen
 * @param modifier [Modifier] for this composable
 */
@Composable
fun <P : Person>  PrepareGameScreen(
    state: PrepareGameScreenState<P>,
    modifier: Modifier = Modifier,
) {
  /*
   A bug in Compose's navigation component makes the system window shrink to the measured size of
   the dialog when it's filled for the first time. On the following recompositions, this new size
   is applied as the constraints to the root of the hierarchy and some elements might not be able
   to occupy some space they need.
   Applying Modifier.fillMaxSize() makes sure we "reserve" this space and that the window will
   never force us to shrink our content.
  */
  Box(modifier.fillMaxSize(), Alignment.Center) {
    PrepareGameDialog(
        state = state,
        modifier = Modifier.padding(vertical = 48.dp),
    )
  }
}
