package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.ui.i18n.Language
import ch.epfl.sdp.mobile.ui.setting.EditLanguageDialog
import ch.epfl.sdp.mobile.ui.setting.EditLanguageDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Implementation of the [EditLanguageDialogState] interface.
 *
 * @property scope the coroutine scope.
 * @property settingFacade the facade to access the settings
 * @param onCloseAction the callback called after we click on Cancel or Save Button.
 */
class EditLanguageDialogStateImpl(
    private val scope: CoroutineScope,
    private val settingFacade: SettingsFacade,
    onCloseAction: State<() -> Unit>,
) : EditLanguageDialogState {
  private val onCloseAction by onCloseAction

  override var selectedLanguage by mutableStateOf(Language.English)

  init {
    scope.launch {
      settingFacade.getLanguage().collect { selectedLanguage = it ?: Language.English }
    }
  }

  override fun onSaveClick() {
    scope.launch {
      settingFacade.setLanguage(selectedLanguage)
      onCloseAction()
    }
  }

  override fun onCancelClick() {
    onCloseAction()
  }
}

/**
 * A stateful implementation of [EditLanguageDialog] which uses some composition-local values to
 * retrieve the appropriate dependencies.
 *
 * @param onClose the callback called after we click on the Cancel or Save Button the changes.
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun StatefulEditLanguageDialog(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val onCloseAction = rememberUpdatedState(onClose)
  val settingsFacade = LocalSettingsFacade.current

  val state =
      remember(scope, onCloseAction, settingsFacade) {
        EditLanguageDialogStateImpl(scope, settingsFacade, onCloseAction)
      }

  EditLanguageDialog(state, modifier)
}
