package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.EditProfileNameDialog
import ch.epfl.sdp.mobile.ui.setting.EditProfileNameDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EditProfileNameDialogStateImpl(
    user: AuthenticatedUser,
    private val scope: CoroutineScope,
    onSaveAction: State<() -> Unit>,
    onCancleAction: State<() -> Unit>
) : EditProfileNameDialogState {
  var user by mutableStateOf(user)
  val onSaveAction by onSaveAction
  val onCancleAction by onCancleAction

  override var name by mutableStateOf(user.name)

  override fun onSaveClick() {
    scope.launch {
      user.update { name(name) }
      onSaveAction()
    }
  }

  override fun onCancleClick() {
    onCancleAction()
  }
}

@Composable
fun StatefulEditProfileDialog(user: AuthenticatedUser, onSave: () -> Unit, onCancle: () -> Unit) {
  val scope = rememberCoroutineScope()
  val onSaveAction = rememberUpdatedState(onSave)
  val onCancleAction = rememberUpdatedState(onCancle)

  val state =
      remember(user, scope, onCancleAction, onCancleAction) {
        EditProfileNameDialogStateImpl(user, scope, onSaveAction, onCancleAction)
      }

  EditProfileNameDialog(state)
}
