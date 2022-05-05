package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/** An interface which represents the state of the user */
@Stable
interface SpeechRecognizerState {

  /** A [Boolean] which indicates if the device is currently listening to voice inputs. */
  val listening: Boolean

  /** A callback which will be invoked when the user clicks on the listening button. */
  fun onListenClick()
}
