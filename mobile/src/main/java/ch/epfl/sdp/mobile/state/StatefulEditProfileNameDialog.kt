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
 * @param onSaveAction the callback called after we saved the changes.
 * @param onCancelAction the callback called when we click the cancel button.
 */
class EditProfileNameDialogStateImpl(
    private val user: AuthenticatedUser,
    private val scope: CoroutineScope,
    onSaveAction: State<() -> Unit>,
    onCancelAction: State<() -> Unit>
) : EditProfileNameDialogState {
  private val onSaveAction by onSaveAction
  private val onCancelAction by onCancelAction

  override var name by mutableStateOf(user.name)

  /**
   * A [MutatorMutex] which enforces mutual exclusion of update profile name requests. Performing a
   * new request (by clicking the button) will cancel the currently pending request.
   */
  private val mutex = MutatorMutex()

  override fun onSaveClick() {
    scope.launch {
      mutex.mutate(MutatePriority.UserInput) {
        user.update { name(name) }
        onSaveAction()
      }
    }
  }

  override fun onCancelClick() {
    onCancelAction()
  }
}

/**
 * A stateful implementation of [EditProfileNameDialog] which uses some composition-local values to
 * retrieve the appropriate dependencies.
 *
 * @param user the current [AuthenticatedUser].
 * @param onSave the callback called after we saved the changes.
 * @param onCancel the callback called when we click the cancel button.
 */
@Composable
fun StatefulEditProfileNameDialog(
    user: AuthenticatedUser,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val onSaveAction = rememberUpdatedState(onSave)
  val onCancelAction = rememberUpdatedState(onCancel)

  val state =
      remember(user, scope, onSaveAction, onCancelAction) {
        EditProfileNameDialogStateImpl(user, scope, onSaveAction, onCancelAction)
      }

  EditProfileNameDialog(state)
}
