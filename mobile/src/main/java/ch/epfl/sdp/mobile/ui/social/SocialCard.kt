package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.toColor

/**
 * This card is used to display player information in the Social screen
 *
 * @param person The [Person] contains the information that need to be displayed
 * @param trailingAction Define the trailing action in the card
 * @param modifier the [Modifier] for the composable
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SocialCard(
    person: Person,
    trailingAction: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

  ListItem(
      modifier = modifier.padding(vertical = 8.dp),
      text = {
        Text(
            person.name,
            color = MaterialTheme.colors.primaryVariant,
            style = MaterialTheme.typography.subtitle1)
      },
      icon = {
        Box(
            modifier =
                Modifier.size(40.dp).clip(CircleShape).background(person.backgroundColor.toColor()),
        ) { Text(person.emoji, modifier = Modifier.align(Alignment.Center)) }
      },
      trailing = { trailingAction() },
  )
}
