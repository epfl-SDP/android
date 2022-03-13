package ch.epfl.sdp.mobile.ui.social

import ch.epfl.sdp.mobile.application.Profile.Color

/** An interface representing a list of people you're following. */
interface Person {

  /** The background color of the profile of this user. */
  val backgroundColor: Color

  /** The name of the user. */
  val name: String

  /** The emoji of the profile picture of the user. */
  val emoji: String
}
