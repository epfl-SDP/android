package ch.epfl.sdp.mobile.ui.game

/** Interface of the text to speech UI. */
interface TextToSpeechState {

  /** Indicates if the text to speech is enabled. */
  val textToSpeechEnabled: Boolean

  /** Callback for the text to speech toggle action. */
  fun onTextToSpeechToggle()
}
