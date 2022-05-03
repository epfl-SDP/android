package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.ui.PawniesColors.Green800
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
        LazyRow() { items(items = emojis) { item -> SelectEmojiItem(state, item) } }
        LazyRow() {
          items(items = Color.asList()) { item -> SelectBackgroundColorItem(state, item) }
        }
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
 * Select Emoji Item
 * @param state which is [EditProfileImageDialogState] and modifies it
 * @param emoji String which to be changed
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun SelectEmojiItem(
    state: EditProfileImageDialogState,
    emoji: String,
    modifier: Modifier = Modifier
) {
  Box(
      modifier =
          if (state.emoji == emoji)
              modifier.size(72.dp).border(BorderStroke(4.dp, Green800), CircleShape)
          else modifier.size(72.dp).clickable { state.emoji = emoji },
      contentAlignment = Alignment.Center,
  ) { Text(emoji, style = MaterialTheme.typography.h4) }
}

/**
 * Select Background color of profile Image
 * @param state which is [EditProfileImageDialogState] and modifies it
 * @param backgroundColor the current color in item
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun SelectBackgroundColorItem(
    state: EditProfileImageDialogState,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
  Box(modifier = modifier.padding(4.dp)) {
    Box(
        modifier =
            if (state.backgroundColor == backgroundColor)
                modifier
                    .size(64.dp)
                    .background(backgroundColor.toColor(), CircleShape)
                    .border(BorderStroke(4.dp, Green800), CircleShape)
            else
                modifier.size(64.dp).background(backgroundColor.toColor(), CircleShape).clickable {
                  state.backgroundColor = backgroundColor
                },
        contentAlignment = Alignment.Center,
    ) {}
  }
}
