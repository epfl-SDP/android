package ch.epfl.sdp.mobile.application

import androidx.compose.ui.graphics.Color

/**
 * An enumeration representing the different colors which can be used as the background of a profile
 * picture.
 */
enum class ProfileColor {
  Pink,
  Blue;

  // TODO : This belongs to a separate package.
  fun getColorForProfile(): Color {
    return when (this) {
      Pink -> Color.Magenta
      Blue -> Color.Blue
    }
  }
}
