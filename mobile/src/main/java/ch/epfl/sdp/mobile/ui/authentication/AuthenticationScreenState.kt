package ch.epfl.sdp.mobile.ui.authentication

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode

/**
 * A state which indicates the contents of an [AuthenticationScreen] composable. It will keep track
 * of the values of the different text fields, as well as the [Mode] in which the authentication
 * screen currently is.
 */
@Stable
interface AuthenticationScreenState {

  /** Indicates whether the user is currently trying to authenticate or to register. */
  enum class Mode {

    /** The user is trying to sign in. */
    LogIn,

    /** The user is trying to sign up. */
    Register,
  }

  /** The current [Mode]. */
  var mode: Mode

  /** True if the authentication button should display a loading indicator. */
  val loading: Boolean

  /** The value of the text in the email field. */
  var email: String

  /** The value of the text in the name field. */
  var name: String

  /** The value of the text in the password field. */
  var password: String

  /** The value to display in the error field. */
  val error: String?

  /** A callback invoked when the user clicks on the authentication button. */
  fun onAuthenticate()
}
