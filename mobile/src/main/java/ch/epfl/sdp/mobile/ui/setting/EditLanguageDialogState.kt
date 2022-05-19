package ch.epfl.sdp.mobile.ui.setting


/** State interface of the [EditLanguageImageDialog] */
interface EditLanguageDialogState {
  /** Language which will be changed */
  var selectedLanguage: String

  /** Emoji String which will be changed */
  var emoji: String

  /** Action to execute when clicking on the Save button */
  fun onSaveClick()

  /** Action to execute when clicking on the Cancel button */
  fun onCancelClick()
}
