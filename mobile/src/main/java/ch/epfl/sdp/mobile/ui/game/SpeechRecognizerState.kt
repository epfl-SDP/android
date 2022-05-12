package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/** An interface which represents the state of the speech recognition user interface. */
@Stable
interface SpeechRecognizerState {

  /**
   * Store the current error value, if there are not error involved the value is set to
   * [SpeechRecognizerError.None]
   */
  var currentError: SpeechRecognizerError

  /** Enum class that defined different error related to the speech recognizer */
  enum class SpeechRecognizerError {

    // FIXME General error, temporary
    InternalError,

    /** The asked command cannot be performed */
    IllegalAction,

    /** The command cannot be parsed */
    UnknownCommand,

    /** None error */
    None,
  }

  /** A [Boolean] which indicates if the device is currently listening to voice inputs. */
  val listening: Boolean

  /** A callback which will be invoked when the user clicks on the listening button. */
  fun onListenClick()
}
