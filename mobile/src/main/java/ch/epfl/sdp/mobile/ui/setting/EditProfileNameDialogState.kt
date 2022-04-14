package ch.epfl.sdp.mobile.ui.setting

/** State interface of the [EditProfileNameDialog] */
interface EditProfileNameDialogState {
  var name: String

  /** Action to execute when clicking on the Save button */
  fun onSaveClick()

  /** Action to execute when clicking on the Cancle button */
  fun onCancleClick()
}
