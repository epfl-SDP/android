package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.prepare_game.Dialog

/**
 * Component for display a Dialog to edit the profile name
 *
 * @param state the [EditProfileNameDialogState] as an argument.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun EditProfileNameDialog(
    state: EditProfileNameDialogState,
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
      cancelContent = { Text(text = strings.settingEditCancle) },
      confirmContent = { Text(text = strings.settingEditSave) },
      onCancelClick = state::onCancleClick,
      onConfirmClick = state::onSaveClick
    ) {
      Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(strings.settingProfileName, style = MaterialTheme.typography.subtitle1)

        TextField(
            value = state.name,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { state.name = it })
      }
    }
  }
}
