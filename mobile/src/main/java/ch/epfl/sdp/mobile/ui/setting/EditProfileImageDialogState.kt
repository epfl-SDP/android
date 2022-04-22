package ch.epfl.sdp.mobile.ui.setting

/** State interface of the [EditProfileImageDialog] */
interface EditProfileImageDialogState {
  /** UserName which will be changed */
  var userName: String

  /** Action to execute when clicking on the Save button */
  fun onSaveClick()

  /** Action to execute when clicking on the Cancel button */
  fun onCancelClick()
}
