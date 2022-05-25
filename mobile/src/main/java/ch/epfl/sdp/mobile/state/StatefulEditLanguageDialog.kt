package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.ui.setting.EditLanguageDialog
import ch.epfl.sdp.mobile.ui.setting.EditLanguageDialogState
import java.util.Locale.ENGLISH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Implementation of the [EditLanguageDialogState] interface
 *
 * @param scope the coroutine scope.
 * @param settingFacade the facade to access the settings
 * @param onCloseAction the callback called after we click on Cancel or Save Button.
 */
class EditLanguageDialogStateImpl(
    private val scope: CoroutineScope,
    private val settingFacade: SettingsFacade,
    onCloseAction: State<() -> Unit>,
) : EditLanguageDialogState {
  private val onCloseAction by onCloseAction

  override var selectedLanguage by mutableStateOf("")

  init {
    scope.launch {
      settingFacade.getLanguage().collect { selectedLanguage = it ?: ENGLISH.language }
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
 */
@Composable
fun StatefulEditLanguageDialog(
    onClose: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val onCloseAction = rememberUpdatedState(onClose)
  val settingsFacade = LocalSettingsFacade.current

  val state =
      remember(scope, onCloseAction) {
        EditLanguageDialogStateImpl(scope, settingsFacade, onCloseAction)
      }

  EditLanguageDialog(state)
}
