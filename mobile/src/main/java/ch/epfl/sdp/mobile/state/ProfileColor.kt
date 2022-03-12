package ch.epfl.sdp.mobile.state

import android.graphics.Color as NativeColor
import androidx.compose.ui.graphics.Color
import ch.epfl.sdp.mobile.application.ProfileColor

/** The [Regex] which can figure out if a string is a valid ARGB hex color. */
private val ARGBPattern = Regex("#[0-9a-fA-F]{8}")

/**
 * Transforms this [ProfileColor] into a compose-friendly [Color].
 *
 * @receiver the [ProfileColor] which is transformed.
 * @return the transformed [Color].
 */
fun ProfileColor.toColor(): Color {
  val color = takeIf { ARGBPattern.matches(hex) } ?: ProfileColor.Default
  return Color(NativeColor.parseColor(color.hex))
}
