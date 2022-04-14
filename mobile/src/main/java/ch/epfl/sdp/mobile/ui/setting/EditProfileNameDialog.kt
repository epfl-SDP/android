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

@Composable
fun EditProfileNameDialog(
    state: EditProfileNameDialogState,
    modifier: Modifier = Modifier,
) {

  Box(modifier.fillMaxSize(), Alignment.Center) {
    val strings = LocalLocalizedStrings.current

    Dialog(
        modifier = modifier,
        cancelContent = { Text(text = strings.settingEditCancle) },
        confirmContent = { Text(text = strings.settingEditSave) },
        onCancelClick = state::onCancleClick,
        onConfirmClick = state::onSaveClick) {
      Column(modifier = modifier.padding(16.dp)) {
        Text(strings.settingProfileName, style = MaterialTheme.typography.subtitle1)
        TextField(
            value = state.name,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { state.name = it })
      }
    }
  }
}
