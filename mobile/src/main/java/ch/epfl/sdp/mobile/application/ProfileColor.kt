package ch.epfl.sdp.mobile.application

/**
 * A class representing the different colors that the profile picture background might take. It may
 * contain arbitrary [String], but some default values are provided in [ProfileColor.Companion].
 *
 * @constructor creates a new [ProfileColor].
 * @param hex the string representation of the color.
 */
data class ProfileColor(val hex: String) {

  companion object {

    /** A soft orange value. */
    val Orange = ProfileColor("#FFF8A68D")

    /** A default color. */
    val Default = Orange
  }
}
