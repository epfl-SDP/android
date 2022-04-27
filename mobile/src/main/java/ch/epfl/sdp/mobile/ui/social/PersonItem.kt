package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * This list item is used to display player information in the Social screen.
 *
 * @param person The [Person] contains the information that need to be displayed
 * @param trailingAction Define the trailing action in the card
 * @param modifier the [Modifier] for the composable
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonItem(
    person: Person,
    modifier: Modifier = Modifier,
    trailingAction: @Composable () -> Unit = {}
) {
  ListItem(
      modifier = modifier,
      icon = {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(person.backgroundColor),
        ) { Text(person.emoji, modifier = Modifier.align(Alignment.Center)) }
      },
      text = {
        Text(
            text = person.name,
            color = MaterialTheme.colors.primaryVariant,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      },
      trailing = trailingAction,
  )
}
