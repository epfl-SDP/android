package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.PawniesColors.Beige050

/**
 * This list item is used to display player information in the Social screen.
 *
 * @param person The [Person] contains the information that need to be displayed
 * @param onShowProfileClick open profile if clicked
 * @param modifier the [Modifier] for the composable
 * @param trailingAction Define the trailing action in the card
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PersonItem(
    person: Person,
    onShowProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingAction: @Composable () -> Unit = {}
) {
  ListItem(
      modifier = Modifier.background(Beige050).clickable { onShowProfileClick() },
      icon = {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(person.backgroundColor)) {
          Text(person.emoji, modifier = Modifier.align(Alignment.Center))
        }
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
