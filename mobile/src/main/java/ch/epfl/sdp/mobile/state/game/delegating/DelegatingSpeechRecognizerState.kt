@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.voice.VoiceInput
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.game.core.MutableGameDelegate
import ch.epfl.sdp.mobile.ui.game.SpeechRecognizerState
import ch.epfl.sdp.mobile.ui.game.SpeechRecognizerState.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of [SpeechRecognizerState] which will display the results in a
 * [SnackbarHostState], and delegates game management to [MutableGameDelegate].
 *
 * @param delegate the underlying [MutableGameDelegate].
 * @param permission the [PermissionState] for the microphone permission.
 * @param facade the [SpeechFacade] which is used.
 * @param snackbarHostState the [SnackbarHostState] used to display some results.
 * @param scope the [CoroutineScope] in which the actions are performed.
 */
class DelegatingSpeechRecognizerState
constructor(
    private val delegate: MutableGameDelegate,
    private val permission: PermissionState,
    private val facade: SpeechFacade,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
) : SpeechRecognizerState {

  override var currentError by mutableStateOf(SpeechRecognizerError.None)

  override var listening: Boolean by mutableStateOf(false)
    private set

  /**
   * A [MutatorMutex] which ensures that multiple speech recognition requests aren't performed
   * simultaneously, and that clicking on the button again cancels the previous request.
   */
  private val mutex = MutatorMutex()

  override fun onListenClick() {
    scope.launch {
      val willCancel = listening
      mutex.mutate(MutatePriority.UserInput) {
        try {
          if (willCancel) return@mutate
          listening = true
          if (!permission.hasPermission) {
            permission.launchPermissionRequest()
          } else {
            when (val speech = facade.recognize()) {
              // TODO : Display an appropriate message, otherwise act on the board.
              SpeechFacade.RecognitionResult.Failure.Internal ->
                  currentError = SpeechRecognizerError.InternalError
              is SpeechFacade.RecognitionResult.Success -> {

                // Parsed the input
                val parsedAction = VoiceInput.parseInput(speech.results)
                if (parsedAction != null) {
                  // Try to perform the action
                  val isSuccessful = delegate.tryPerformAction(parsedAction)

                  // If the action is illegal
                  if (!isSuccessful) currentError = SpeechRecognizerError.IllegalAction
                } else { // If cannot be parsed
                  currentError = SpeechRecognizerError.UnknownCommand
                }
              }
            }
          }
        } finally {
          listening = false
        }
      }
    }
  }
}
