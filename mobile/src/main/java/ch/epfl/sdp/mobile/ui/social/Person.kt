package ch.epfl.sdp.mobile.ui.social

import androidx.compose.ui.graphics.Color

/** An interface representing a list of people you're following. */
interface Person {

  /** The background color of the profile of this user. */
  val backgroundColor: Color

  /** The name of the user. */
  val name: String

  /** The emoji of the profile picture of the user. */
  val emoji: String

  /** True iff the current user is following this [Person]. */
  val followed: Boolean
}
