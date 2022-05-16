package ch.epfl.sdp.mobile.ui

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import io.github.sceneview.utils.colorOf

/** The palette of colors used in the Pawnies application. */
val PawniesLightColors =
    lightColors(
        primary = PawniesColors.Green800,
        onPrimary = PawniesColors.Green100,
        primaryVariant = PawniesColors.Green500,
        secondary = PawniesColors.Orange500,
        surface = PawniesColors.Beige050,
        onSurface = PawniesColors.Green800,
        background = PawniesColors.Beige050,
        onBackground = PawniesColors.Green800,
    )

object PawniesColors {
  val Beige050 = Color(0xFFFFFBE6)
  val Green100 = Color(0xFFB9E4C9)
  val Green200 = Color(0xFFA9DBBB)
  val Green500 = Color(0xFF379665)
  val Green800 = Color(0xFF356859)
  val Orange500 = Color(0xFFFD5523)
  val Orange250 = Color(0xFFFD906F)
  val Orange200 = Color(0xFFF8A68D)
}

object PawniesArColors {
  // ArModel Color
  val White = colorOf(1f, 0.99f, 0.94f)
  val Black = colorOf(53 / 255f, 56 / 255f, 57 / 255f)
}
