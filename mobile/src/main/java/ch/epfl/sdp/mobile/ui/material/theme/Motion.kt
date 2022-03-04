package ch.epfl.sdp.mobile.ui.material.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Adapted from https://gist.github.com/c5inco/796edc5d88e8561872380b61e6089c04

/**
 * An [EnterTransition] for the shared axis motion pattern on the Y axis.
 *
 * @param slideDistance the quantity of [Dp] that the content will slide with.
 */
@Composable
fun sharedYAxisEnterTransition(slideDistance: Dp = SharedAxisSlideDistance): EnterTransition {
  val offset = with(LocalDensity.current) { slideDistance.roundToPx() }
  return fadeIn(
      animationSpec = tween(210, delayMillis = 90, easing = LinearOutSlowInEasing),
  ) +
      slideInVertically(
          initialOffsetY = { offset },
          animationSpec = tween(durationMillis = 300),
      )
}

/**
 * An [ExitTransition] for the shared axis motion pattern on the Y axis.
 *
 * @param slideDistance the quantity of [Dp] that that content will slide with.
 */
@Composable
fun sharedYAxisExitTransition(slideDistance: Dp = SharedAxisSlideDistance): ExitTransition {
  val offset = with(LocalDensity.current) { slideDistance.roundToPx() }
  return fadeOut(
      animationSpec = tween(90, easing = FastOutLinearInEasing),
  ) +
      slideOutVertically(
          targetOffsetY = { -offset },
          animationSpec = tween(durationMillis = 300),
      )
}

/** The default slide distance for shared axis motion patterns. */
val SharedAxisSlideDistance = 30.dp
