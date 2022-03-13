package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.PawniesIcons

/**
 * This composable display all the players that are in the [SearchState]. This composable also allow
 * user to follow another player
 *
 * @param state the [SearchState], manage the composable contents
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun SearchResultList(state: SearchState, modifier: Modifier = Modifier) {
  LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(state.players) { player ->
      PersonCard(
          person = player,
          trailingAction = {
            OutlinedButton(
                onClick = { /*TODO*/},
                shape = RoundedCornerShape(24.dp),
            ) {
              Icon(
                  PawniesIcons.Add, contentDescription = LocalLocalizedStrings.current.socialFollow)

              Text(
                  modifier = Modifier.padding(start = 8.dp),
                  text = LocalLocalizedStrings.current.socialFollow,
              )
            }
          })
    }
  }
}
