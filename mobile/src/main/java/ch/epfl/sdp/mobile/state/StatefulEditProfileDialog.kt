package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.EditProfileNameDialog
import ch.epfl.sdp.mobile.ui.setting.EditProfileNameDialogState

class EditProfileNameDialogStateImpl(name: String) : EditProfileNameDialogState {
  override var name by mutableStateOf(name)
}

@Composable
fun StatefulEditProfileDialog(
    user: AuthenticatedUser,
) {

  val state = remember() {EditProfileNameDialogStateImpl(user.name)}

  EditProfileNameDialog(state)
}
