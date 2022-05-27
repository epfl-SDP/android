package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
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
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Spacer(Modifier.height(16.dp))
        EditSettingPicture(
            backgroundColor = state.backgroundColor,
            emoji = state.emoji,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Text(
            strings.settingProfileImageLabel,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
          items(items = Emojis) { item ->
            SelectEmojiItem(selected = state.emoji == item, onClick = { state.emoji = item }, item)
          }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
          items(items = Color.values) { item ->
            SelectBackgroundColorItem(
                selected = state.backgroundColor == item,
                onClick = { state.backgroundColor = item },
                backgroundColor = item,
                modifier = Modifier.padding(4.dp))
          }
        }
        Spacer(Modifier.height(16.dp))
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
private fun EditSettingPicture(
    backgroundColor: Color,
    emoji: String,
    modifier: Modifier = Modifier
) {

  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {
    Box(
        modifier = Modifier.size(118.dp).background(backgroundColor.toColor(), CircleShape),
        contentAlignment = Alignment.Center,
    ) { Text(emoji, style = MaterialTheme.typography.h3) }
  }
}

/**
 * Select Emoji Item
 * @param selected boolean which check if current element is selected
 * @param onCLick function to execute if current element is clicked
 * @param emoji String which to be changed
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun SelectEmojiItem(
    selected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    modifier: Modifier = Modifier
) {
  Box(
      modifier =
          if (selected) modifier.size(72.dp).border(BorderStroke(4.dp, Green800), CircleShape)
          else modifier.size(72.dp).clickable { onClick() },
      contentAlignment = Alignment.Center,
  ) { Text(emoji, style = MaterialTheme.typography.h4) }
}

/**
 * Select Background color of profile Image
 * @param selected boolean if current element is equal current item
 * @param onClick function to execute if element is clicked
 * @param backgroundColor the current color in item
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun SelectBackgroundColorItem(
    selected: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
  Box(
      modifier
          .size(64.dp)
          .background(backgroundColor.toColor(), CircleShape)
          .then(
              if (selected) Modifier.border(BorderStroke(4.dp, Green800), CircleShape)
              else Modifier.clip(CircleShape).clickable { onClick() }))
}
