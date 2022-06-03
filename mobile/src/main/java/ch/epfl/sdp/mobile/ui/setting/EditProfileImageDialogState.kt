package ch.epfl.sdp.mobile.ui.setting

import ch.epfl.sdp.mobile.application.Profile.Color

/** State interface of the [EditProfileImageDialog]. */
interface EditProfileImageDialogState {

  /** Background Color which will be changed. */
  var backgroundColor: Color

  /** Emoji String which will be changed. */
  var emoji: String

  /** Action to execute when clicking on the Save button. */
  fun onSaveClick()

  /** Action to execute when clicking on the Cancel button. */
  fun onCancelClick()
}
