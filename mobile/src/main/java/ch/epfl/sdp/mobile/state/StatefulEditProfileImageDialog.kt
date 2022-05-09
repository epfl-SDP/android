package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.EditProfileImageDialog
import ch.epfl.sdp.mobile.ui.setting.EditProfileImageDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Implementation of the [EditProfileImageDialogState] interface
 *
 * @param user the current [AuthenticatedUser].
 * @param scope the coroutine scope.
 * @param onCloseAction the callback called if dialog need to be closed.
 */
class EditProfileImageDialogStateImpl(
    private val user: AuthenticatedUser<*, *>,
    private val scope: CoroutineScope,
    onCloseAction: State<() -> Unit>,
) : EditProfileImageDialogState {
  private val onCloseAction by onCloseAction

  override var emoji by mutableStateOf(user.emoji)
  override var backgroundColor by mutableStateOf(user.backgroundColor)

  /**
   * A [MutatorMutex] which enforces mutual exclusion of update profile image requests. Performing a
   * new request (by clicking the button) will cancel the currently pending request.
   */
  private val mutex = MutatorMutex()

  override fun onSaveClick() {
    scope.launch {
      mutex.mutate(MutatePriority.UserInput) {
        user.update {
          emoji(emoji)
          backgroundColor(backgroundColor)
        }
        onCloseAction()
      }
    }
  }

  override fun onCancelClick() {
    onCloseAction()
  }
}

/**
 * A stateful implementation of [EditProfileImageDialog] which uses some composition-local values to
 * retrieve the appropriate dependencies.
 *
 * @param user the current [AuthenticatedUser].
 * @param onClose the callback called after we click the close and the save button.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulEditProfileImageDialog(
    user: AuthenticatedUser<*, *>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val onCloseAction = rememberUpdatedState(onClose)

  val state =
      remember(user, scope, onCloseAction) {
        EditProfileImageDialogStateImpl(user = user, scope = scope, onCloseAction = onCloseAction)
      }

  EditProfileImageDialog(state, modifier)
}
