package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * This card is used to display a past chess game
 *
 * @param modifier the [Modifier] for the composable
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PastGame(
    chessMatch: ChessMatch,
    modifier: Modifier = Modifier,
) {

  ListItem(
      modifier = modifier,
      text = {},
  )
}
