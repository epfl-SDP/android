package ch.epfl.sdp.mobile.ui.setting

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.prepare_game.Dialog
import ch.epfl.sdp.mobile.ui.prepare_game.SelectableItem

/**
 * Component for display a Dialog to edit the applicationLanguage
 *
 * @param state the [EditLanguageDialogState] as an argument.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun EditLanguageDialog(
    state: EditLanguageDialogState,
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
    Log.i("myinfo", "in component ${state.selectedLanguage}")
    Dialog(
        cancelContent = { Text(text = strings.settingEditCancel) },
        confirmContent = { Text(text = strings.settingEditSave) },
        onCancelClick = state::onCancelClick,
        onConfirmClick = state::onSaveClick) {
      Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(strings.settingLanguageLabel, style = MaterialTheme.typography.subtitle1)

        LanguageList(Languages, state.selectedLanguage, { state.selectedLanguage = it }, Modifier)
      }
    }
  }
}

@Composable
fun LanguageList(
    languages: Map<String, String>,
    selectedLanguage: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier
) {
  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
  ) {
    languages.forEach {
      SelectableItem(
          onClick = { onItemClick(it.value) },
          it.value == selectedLanguage,
          modifier.fillMaxWidth()) { Text(it.key) }
    }
  }
}
