package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.EditProfileNameDialog
import ch.epfl.sdp.mobile.ui.setting.EditProfileNameDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Implementation of the [EditProfileNameDialogState] interface
 *
 * @param user the current [AuthenticatedUser].
 * @param scope the coroutine scope.
 * @param onCloseAction the callback called after we click on Cancel or Save Button.
 */
class EditLanguageDialogStateImpl(
    private val user: AuthenticatedUser,
    private val scope: CoroutineScope,
    onCloseAction: State<() -> Unit>,
) : EditProfileNameDialogState {
  private val onCloseAction by onCloseAction

  override var userName by mutableStateOf(user.name)

  /**
   * A [MutatorMutex] which enforces mutual exclusion of update profile name requests. Performing a
   * new request (by clicking the button) will cancel the currently pending request.
   */
  private val mutex = MutatorMutex()

  override fun onSaveClick() {
    scope.launch {
      mutex.mutate(MutatePriority.UserInput) {
        user.update { name(userName) }
        onCloseAction()
      }
    }
  }

  override fun onCancelClick() {
    onCloseAction()
  }
}

/**
 * A stateful implementation of [EditProfileNameDialog] which uses some composition-local values to
 * retrieve the appropriate dependencies.
 *
 * @param user the current [AuthenticatedUser].
 * @param onClose the callback called after we click on the Cancel or Save Button the changes.
 */
@Composable
fun StatefulEditLanguageDialog(
    user: AuthenticatedUser,
    onClose: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val onCloseAction = rememberUpdatedState(onClose)

  val state =
      remember(user, scope, onCloseAction) {
        EditProfileNameDialogStateImpl(user, scope, onCloseAction)
      }

  EditProfileNameDialog(state)
}
