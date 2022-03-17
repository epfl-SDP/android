package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

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

  val following: List<Person>

  val searchResult: List<Person>

  /** The current [Mode] */
  var mode: Mode

  /** The [List] of players that need to be displayed */

  /** The current user input in the search bar */
  var input: String

  var searchFieldInteraction: MutableInteractionSource

  /** A callback invoked when the user types in the search text field */
  fun onValueChange()

  /**
   * A callback invoked when the user follows another user
   *
   * @param followed A [Person] that the current user wants to follow
   */
  fun onFollow(followed: Person)
}
