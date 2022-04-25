package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.ui.prepare_game.Dialog

/**
 * Component for display a Dialog to edit the profile image
 *
 * @param state the [EditProfileImageDialogState] as an argument.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun EditProfileImageDialog(
    state: EditProfileImageDialogState,
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
    val strings = LocalLocalizedStrings.current

    Dialog(
        cancelContent = { Text(text = strings.settingEditCancel) },
        confirmContent = { Text(text = strings.settingEditSave) },
        onCancelClick = state::onCancelClick,
        onConfirmClick = state::onSaveClick) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditSettingPicture(backgroundColor = state.backgroundColor, emoji = state.emoji)
        Text(strings.settingProfileNameLabel, style = MaterialTheme.typography.subtitle1)

      }
    }
  }
}
/**
 * Composes the edit settings picture which shows the edit changes
 * @param backgroundColor background color for setting image
 * @param emoji the emoji string
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun EditSettingPicture(backgroundColor: Color, emoji: String, modifier: Modifier = Modifier) {

  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
    Box(
      modifier = modifier.size(118.dp).background(backgroundColor.toColor(), CircleShape),
      contentAlignment = Alignment.Center,
    ) { Text(emoji, style = MaterialTheme.typography.h3) }
  }

}


/**
* Composes the edit the emoji
* @param state which is [EditProfileImageDialogState] and modifies it
* @param modifier the [Modifier] for this composable.
*/
@Composable
fun SelectEmoji(state: EditProfileImageDialogState, modifier: Modifier = Modifier) {
  val items = listOf(":)", ":|", "^^")
  LazyRow(items)

}