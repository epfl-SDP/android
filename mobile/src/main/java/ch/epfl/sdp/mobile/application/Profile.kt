package ch.epfl.sdp.mobile.application

/**
 * Represents a [Profile] in the application, which is associated gives information about a user and
 * how they should be displayed to other players.
 */
interface Profile {

  /** The emoji for this profile. */
  val emoji: String

  /** The human-readable name for this profile. */
  val name: String

  /** The background color associated to the [Profile]. */
  val backgroundColor: Color

  /** The unique identifier of the person. */
  val uid: String

  /** True iff this profile is followed by the current user. */
  val followed: Boolean

  /** The list of solved puzzle's ids by the [Profile] */
  val solvedPuzzles: List<String>

  /**
   * A class representing the different colors that the profile picture background might take. It
   * may contain arbitrary [String], but some default values are provided in [Color.Companion].
   *
   * @constructor creates a new [Color].
   * @param hex the string representation of the color.
   */
  data class Color(val hex: String) {

    companion object {

      /** A soft orange value. */
      val Orange = Color("#FFF8A68D")

      /** A default color. */
      val Default = Orange
    }
  }
}
