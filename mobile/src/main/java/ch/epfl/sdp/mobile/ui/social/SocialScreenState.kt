package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.interaction.MutableInteractionSource
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

  /** The [List] of followers that need to be displayed */
  val following: List<Person>

  /** The [List] of search results that getting displayed if search is activated */
  val searchResult: List<Person>

  /** The current [Mode] */
  var mode: Mode

  /** The current user input in the search bar */
  var input: String

  /** Flow for interaction on search field */
  val searchFieldInteraction: MutableInteractionSource

  /** Callback function to open profile of person */
  val openProfile: (Person)->Unit

  /** A callback invoked when the user type in the search text field */
  fun onValueChange()
}
