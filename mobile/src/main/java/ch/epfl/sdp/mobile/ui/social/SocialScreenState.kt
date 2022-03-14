package ch.epfl.sdp.mobile.ui.social

import androidx.compose.runtime.Stable

/**
 * A state which indicates the content of an [SocialScreen] composable. It will keep track of the
 * values of research text field.
 *
 * This state contains a [Mode] that describe which screen to display
 */
@Stable
interface SocialScreenState {

  /** Indicate if displayed screen is the Following or the Searching screen */
  enum class Mode {

    /** The user can scroll a list containing the followed player */
    Following,

    /** The user is searching in the search bar */
    Searching,
  }

  /** The current [Mode] */
  var mode: Mode

  /** The [List] of players that need to be displayed */
  var players: List<Person>

  /** The current user input in the searc bar */
  var input: String

  /** A callback invoked when the user type in the search text field */
  fun onValueChange()
}
