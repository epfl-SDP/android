package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.Profile
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
 * @param onSaveAction the callback called after we saved the changes.
 * @param onCancelAction the callback called when we click the cancel button.
 */
class EditProfileImageDialogStateImpl(
  private val user: AuthenticatedUser,
  private val scope: CoroutineScope,
  onSaveAction: State<() -> Unit>,
  onCancelAction: State<() -> Unit>,
  override var backgroundColor: Profile.Color,
  override var emoji: String
) : EditProfileImageDialogState {
  private val onSaveAction by onSaveAction
  private val onCancelAction by onCancelAction

  /**
   * A [MutatorMutex] which enforces mutual exclusion of update profile image requests. Performing a
   * new request (by clicking the button) will cancel the currently pending request.
   */
  private val mutex = MutatorMutex()

  override fun onSaveClick() {
    scope.launch {
      mutex.mutate(MutatePriority.UserInput) {
        onSaveAction()
      }
    }
  }

  override fun onCancelClick() {
    onCancelAction()
  }
}

/**
 * A stateful implementation of [EditProfileImageDialog] which uses some composition-local values to
 * retrieve the appropriate dependencies.
 *
 * @param user the current [AuthenticatedUser].
 * @param onSave the callback called after we saved the changes.
 * @param onCancel the callback called when we click the cancel button.
 */
@Composable
fun StatefulEditProfileImageDialog(
    user: AuthenticatedUser,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val onSaveAction = rememberUpdatedState(onSave)
  val onCancelAction = rememberUpdatedState(onCancel)

  val state =
      remember(user, scope, onSaveAction, onCancelAction) {
        EditProfileImageDialogStateImpl(
          user = user, scope = scope,
          onSaveAction = onSaveAction,
          onCancelAction = onCancelAction, backgroundColor = Profile.Color.Default, emoji =  "😎")
      }

  EditProfileImageDialog(state)
}
