package ch.epfl.sdp.mobile.ui.branding

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Provides the theming elements of the Pawnies design system, and applies them to the
 * [MaterialTheme] components used within [content].
 *
 * @param content the body of the composable, in which the theme will be applied.
 */
@Composable
fun PawniesTheme(
    content: @Composable () -> Unit,
) {
  MaterialTheme(
      colors = PawniesLightColors,
      typography = PawniesTypography,
      shapes = PawniesShapes,
      content = content,
  )
}
