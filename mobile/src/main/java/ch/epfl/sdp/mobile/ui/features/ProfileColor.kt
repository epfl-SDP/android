package ch.epfl.sdp.mobile.ui.features

import androidx.compose.ui.graphics.Color

/**
 * An enumeration representing the different colors which can be used as the background of a profile
 * picture.
 */
enum class ProfileColor {
  Pink,
  Blue;

  fun getColorForProfile(): Color {
    return when (this) {
      Pink -> Color.Magenta
      Blue -> Color.Blue
    }
  }
}
