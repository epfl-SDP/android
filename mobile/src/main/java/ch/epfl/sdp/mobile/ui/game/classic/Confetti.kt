// Adapted from https://gist.github.com/alexandrepiveteau/9c20c9f0cbbd8efefa4323c9a60b9007

package ch.epfl.sdp.mobile.ui.game.classic

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpOffset.Companion.Zero
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Creates a [ConfettiState] and remembers it in the composition. */
@Composable
fun rememberConfettiState(): ConfettiState {
  return remember { SnapshotConfettiState() }
}

/**
 * An interface describing the state of the confetti which are currently displayed on the screen.
 * Spawning confetti is done with some suspending animation functions.
 */
@Stable
interface ConfettiState {

  /** Returns true if at least one confetti is currently being displayed. */
  val isRunning: Boolean

  /**
   * Spawns a given count of confetti with a certain direction, a certain spread angle, and a given
   * count.
   *
   * The suspending function will resume once all the confetti have decayed and will have been
   * removed.
   *
   * @param angle the angle at which the confetti are fired. Should be between 0 and 360 degrees.
   * @param spread the width of the firing spread. Coerced between 0 and 360 degrees.
   * @param count the number of confetti which will be spawned.
   * @param colors the colors of the confetti.
   */
  suspend fun spawn(
      angle: Float = 90f,
      spread: Float = 90f,
      count: Int = 40,
      colors: List<Color> = Colors,
  )

  /**
   * Returns a [ConfettiInstance], which should be used to perform the rendering of the confetti on
   * the canvas.
   */
  @Composable fun rememberUpdatedInstance(): ConfettiInstance
}

/** A renderable instance of the [ConfettiState]. */
fun interface ConfettiInstance {

  /** Draws the confetti using the provided [ContentDrawScope]. */
  fun ContentDrawScope.drawConfetti()
}

/**
 * A [Modifier] which draws a bunch of confetti on the screen, controlled by the provided
 * [ConfettiState] instance.
 *
 * @param state the [ConfettiState] which will be drawn.
 */
fun Modifier.confetti(
    state: ConfettiState,
): Modifier = composed {
  val instance = state.rememberUpdatedInstance()
  drawWithContent {
    with(instance) { drawConfetti() }
    drawContent()
  }
}

/**
 * Continuously spawn one confetti per second during a [timeMillis] duration.
 *
 * @param timeMillis the duration of the confetti spawn.
 * @see ConfettiState.spawn for information about the other spawn parameters.
 */
suspend fun ConfettiState.party(
    timeMillis: Long,
    angle: Float = 90f,
    spread: Float = 90f,
    colors: List<Color> = Colors,
): Unit = coroutineScope {
  var now = withFrameMillis { it }
  val end = now + timeMillis
  while (now < end) {
    now = withFrameMillis { it }
    launch { spawn(angle = angle, spread = spread, count = 1, colors = colors) }
  }
}

/**
 * A class representing the state of a single confetti particle. Each particle will handle its own
 * properties, physics and rendering.
 */
private class Confetti(
    val color: Color,
    val initialSpeed: DpOffset,
    val targetSlide: DpOffset,
    val isCircular: Boolean = Random.nextFloat() < 0.2f,
    val rotationFactor: Float = Random.nextFloat() - 0.5f,
    val scaleFactor: Float = 2f * (Random.nextFloat() - 0.5f),
    val boost: Animatable<DpOffset, AnimationVector2D> = Animatable(Zero, DpOffset.VectorConverter),
    val slide: Animatable<DpOffset, AnimationVector2D> = Animatable(Zero, DpOffset.VectorConverter),
    val opacity: Animatable<Float, AnimationVector1D> = Animatable(1f, Float.VectorConverter),
) {

  /** Animates the provided [Confetti] until it's hidden and done animating. */
  suspend fun fire(): Unit = coroutineScope {
    launch { boost.animateDecay(initialSpeed, exponentialDecay()) }
    launch { slide.animateTo(targetSlide, tween(Duration, easing = FastOutLinearInEasing)) }
    launch { opacity.animateTo(0f, tween(Duration, easing = LinearEasing)) }
  }

  /**
   * Renders the provided confetti on the [ContentDrawScope], using the different properties for
   * this specific particle.
   */
  fun ContentDrawScope.render() {
    val color = color.copy(alpha = opacity.value)
    val rotation = opacity.value * Rotations * rotationFactor * 360f
    val offset = boost.value + slide.value
    val offsetX = offset.x.toPx()
    val offsetY = offset.y.toPx()
    val scaleY = cos(opacity.value * Rotations * scaleFactor)

    withTransform(
        transformBlock = {
          translate(left = offsetX, top = offsetY)
          rotate(rotation)
          scale(scaleX = 1f, scaleY = scaleY)
        },
    ) {
      if (isCircular) {
        drawCircle(color = color, radius = 4.dp.toPx())
      } else {
        val size = Size(8.dp.toPx(), 8.dp.toPx())
        val topLeft = center - Offset(4.dp.toPx(), 4.dp.toPx())
        drawRect(color = color, topLeft = topLeft, size = size)
      }
    }
  }
}

/** An implementation of [ConfettiState] that makes use of snapshots to keep confetti state. */
private class SnapshotConfettiState : ConfettiState {

  /** Ensures mutually exclusive access to [confetti]. */
  private val mutex = Mutex()

  /** The [Confetti] which should be rendered. */
  private val confetti = mutableStateListOf<Confetti>()

  override val isRunning
    get() = confetti.isNotEmpty()

  /**
   * Spawns a single confetti, and runs it in a suspending fashion.
   *
   * @param angle the angle which the spawn cone targets.
   * @param spread the width of the spawn cone.
   * @param colors the possible colors for the confetti.
   */
  private suspend fun spawnOne(angle: Float, spread: Float, colors: List<Color>) {
    check(angle >= 0f && angle < 360f) { "Angle must be a valid angle." }
    check(spread > 0f && spread <= 360f) { "Spread must be a valid angle." }
    check(colors.isNotEmpty()) { "Must provide at least one confetti color." }

    val firstAngle = angle.toDouble() - spread / 2f
    val secondAngle = angle.toDouble() + spread / 2f
    val fromAngle = minOf(firstAngle, secondAngle)
    val untilAngle = maxOf(firstAngle, secondAngle)

    val confettiAngle = Random.nextDouble(fromAngle, untilAngle).toFloat()
    val confettiDistance = Random.nextDouble(from = 12.0, until = 36.0)

    val x = (cos(2 * PI * confettiAngle / 360f) * confettiDistance).dp
    val y = (-sin(2 * PI * confettiAngle / 360f) * confettiDistance).dp

    val state =
        Confetti(
            color = colors.random(),
            initialSpeed = DpOffset(x * 30, y * 30),
            targetSlide = DpOffset(0.dp, 180.dp),
        )

    try {
      mutex.withLock { confetti.add(state) }
      state.fire()
    } finally {
      withContext(NonCancellable) { mutex.withLock { confetti.remove(state) } }
    }
  }

  override suspend fun spawn(
      angle: Float,
      spread: Float,
      count: Int,
      colors: List<Color>,
  ): Unit = coroutineScope {
    check(count >= 0) { "Must spawn a positive confetti count." }
    repeat(count) { launch { spawnOne(angle, spread, colors) } }
  }

  @Composable
  override fun rememberUpdatedInstance(): ConfettiInstance {
    return remember { ConfettiInstance { confetti.forEach { with(it) { render() } } } }
  }
}

private const val Rotations = 10
private const val Duration = 2400
private val Colors =
    listOf(
        Color(0xFF26ccff),
        Color(0xFFa25afd),
        Color(0xFFff5e7e),
        Color(0xFF88ff5a),
        Color(0xFFfcff42),
        Color(0xFFffa62d),
        Color(0xFFff36ff),
    )

@Preview
@Composable
private fun ConfettiPreview() {
  val confetti = rememberConfettiState()
  val scope = rememberCoroutineScope()
  Box(Modifier.fillMaxSize(), Alignment.Center) {
    Button(
        onClick = {
          scope.launch {
            // confetti.spawn() // Spawn a bunch of confetti.
            confetti.party(2000L) // Spawn 1 confetti per frame for 2000 ms.
          }
        },
        modifier = Modifier.confetti(confetti),
    ) { Text("Confetti party !!") }
  }
}
