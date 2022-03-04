package ch.epfl.sdp.mobile.ui.branding

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

/** The palette of colors used in the Pawnies application. */
val PawniesLightColors =
    lightColors(
        primary = PawniesColors.Green800,
        onPrimary = PawniesColors.Green100,
        secondary = PawniesColors.Orange500,
        surface = PawniesColors.Beige050,
        onSurface = PawniesColors.Green800,
    )

object PawniesColors {
  val Beige050 = Color(0xFFFFFBE6)
  val Green100 = Color(0xFFB9E4C9)
  val Green800 = Color(0xFF356859)
  val Orange500 = Color(0xFFFD5523)
}
