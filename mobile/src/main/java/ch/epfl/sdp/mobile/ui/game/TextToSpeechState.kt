package ch.epfl.sdp.mobile.ui.game

interface TextToSpeechState {
  val textToSpeechEnabled: Boolean
  fun onTextToSpeechToggle()
}
