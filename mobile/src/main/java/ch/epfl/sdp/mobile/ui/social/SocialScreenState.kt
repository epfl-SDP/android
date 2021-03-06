package ch.epfl.sdp.mobile.ui.social

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode

/**
 * A state which indicates the content of an [SocialScreen] composable. It will keep track of the
 * values of research text field.
 *
 * This state contains a [Mode] that describe which screen to display and a [Person] used to have an
 * additional uid parameter.
 *
 * @param P the type of the [Person].
 */
@Stable
interface SocialScreenState<P : Person> {

  /** Indicate if displayed screen is the Following or the Searching screen. */
  enum class Mode {

    /** The user can scroll a list containing the followed player. */
    Following,

    /** The user is searching in the search bar. */
    Searching,
  }

  /** The [List] of followers that need to be displayed. */
  val following: List<P>

  /** The [List] of search results that getting displayed if search is activated. */
  val searchResult: List<P>

  /** The current [Mode]. */
  val mode: Mode

  /** The current user input in the search bar. */
  var input: String

  /** Flow for interaction on search field. */
  val searchFieldInteraction: MutableInteractionSource

  /**
   * Callback function to open profile of person.
   * @param person the type of the [Person] which open the Profile.
   */
  fun onShowProfileClick(person: P)

  /**
   * Callback function to open the [PrepareGameScreen] dialog.
   *
   * @param opponent the person that we want to play against
   */
  fun onPlayClick(opponent: P)

  /**
   * A callback invoked when the user follows another user.
   *
   * @param followed A [P] that the current user wants to follow.
   */
  fun onFollowClick(followed: P)

  /**
   * A callback invoked when the user unfollows another user.
   *
   * @param follower A [P] that the current user wants to unfollow.
   */
  fun onUnfollowClick(follower: P)
}
