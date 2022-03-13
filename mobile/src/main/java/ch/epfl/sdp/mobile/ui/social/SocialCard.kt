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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.ProfileColor

/**
 * This card is used to display player information in the Social screen, The associate button is
 * different according to [mode] value
 *
 * @param person The [Person] contains the information that need to be displayed
 * @param mode [SocialMode] define how the button visual and actions
 * @param modifier the [Modifier] for the composable
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SocialCard(
    person: Person,
    trailingAction: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
  /**
   * This function transform the user profile color into a [Color] that can be used in a composable
   *
   * TODO : Need to define how to stock the color in the class [Person]
   *
   * @param color the profile image background defined by the user
   */
  fun getBackgroundRGB(color: ProfileColor): Color {
    if (color == ProfileColor.Pink) {
      return Color.Magenta
    }
    return Color.Black
  }

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
                Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(getBackgroundRGB(person.backgroundColor)),
        ) { Text(person.emoji, modifier = Modifier.align(Alignment.Center)) }
      },
      trailing = { trailingAction() },
  )
}