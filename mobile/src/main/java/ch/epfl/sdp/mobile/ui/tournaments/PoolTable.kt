package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesTheme
import kotlin.math.roundToInt

/** Represents an amount of points from a given player. */
typealias PoolScore = Int

/** A pool member is a participant of the pool. */
@Stable
interface PoolMember {

  /** The name of the participant. */
  val name: String

  /** The total score of the participant, or null if the pool has not started yet. */
  val total: PoolScore?
}

/**
 * The information displayed within the pool.
 *
 * @param P the type of the actual [PoolMember]s.
 */
@Stable
interface PoolData<P : PoolMember> {

  /** Returns a [List] of all the participants to this pool. */
  val members: List<P>

  /**
   * Returns the score of [this] participant against an [other] participant, or null if the pool has
   * not started yet.
   *
   * @receiver the current participant.
   * @param other the other participant.
   * @return the score, or null if the pool has not started yet.
   */
  fun P.scoreAgainst(other: P): PoolScore?
}

/**
 * The table of the players which are participating in a pool.
 *
 * @param P the type of the [PoolMember] which participate in the pool.
 * @param data the [PoolData] which must be displayed.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <P : PoolMember> PoolTable(
    data: PoolData<P>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.overline,
) {
  ProvideTextStyle(textStyle) {
    CompositionLocalProvider(LocalContentColor provides PawniesColors.Green800) {
      PoolContent(
          players = data.members,
          modifier = modifier.border(1.dp, Color.Cyan),
          // TODO : Use a Text with a maximum width and ellipsis.
          playerContent = { Text(it.name.uppercase()) },
          // TODO : Use a Text with a maximum width and ellipsis.
          scoreTitleContent = { Text("Score".uppercase()) },
          // TODO : Animated score cells !!!
          scoreContent = { index, it ->
            Cell(
                background = color(index),
                modifier = Modifier.fillMaxSize(),
            ) { it.total?.let { score -> Text(score.toString()) } }
          },
      ) { from, to ->
        val score = with(data) { from.scoreAgainst(to) }
        // TODO : Animated score cells !!!
        score?.let { Text(it.toString()) }
      }
    }
  }
}

@Composable
fun <T> PoolContent(
    players: List<T>,
    modifier: Modifier = Modifier,
    spacing: Dp = 16.dp,
    playerContent: @Composable (T) -> Unit,
    scoreTitleContent: @Composable () -> Unit,
    scoreContent: @Composable BoxScope.(Int, T) -> Unit,
    itemContent: @Composable BoxScope.(from: T, to: T) -> Unit,
) {
  Layout(
      content = {
        // Compose all the players (horizontally)
        players.fastForEach { player -> Box(Modifier) { playerContent(player) } }
        // Compose all the players (vertically)
        players.fastForEach { player ->
          Box(
              Modifier.border(1.dp, Color.Green)
                  .rotate(DefaultVerticalTextAngle)
                  .border(1.dp, Color.Red),
          ) { playerContent(player) }
        }
        // Compose the score results
        // TODO : This should be in its own color, to reduce code complexity.
        players.fastForEachIndexed { i, player -> Box(Modifier) { scoreContent(i, player) } }
        // Compose the score title
        Box(Modifier.rotate(DefaultVerticalTextAngle)) { scoreTitleContent() }
        // Compose the results grid
        Grid(players, Modifier.clip(DefaultGridShape), itemContent)
      },
      modifier = modifier,
  ) { measurables, constraints ->
    // For simplicity, fetch all the measurables by group.
    val hPlayersMeasurables = measurables.subList(0, players.size)
    val vPlayersMeasurables = measurables.subList(players.size, 2 * players.size)
    val scoreByPlayerMeasurables = measurables.subList(2 * players.size, 3 * players.size)
    val scoreMeasurable = measurables[3 * players.size]
    val gridMeasurable = measurables[3 * players.size + 1]
    val spacingPx = spacing.toPx()

    // Compute the max intrinsic width of the texts.
    val maxHTextWidth =
        hPlayersMeasurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 0
    val scoreWidth = scoreMeasurable.maxIntrinsicWidth(constraints.maxHeight)
    val maxVTextWidth =
        maxOf(
            scoreWidth,
            vPlayersMeasurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) }
                ?: scoreWidth,
        )

    // Compute the available space.
    val hSpace = (constraints.maxWidth - maxHTextWidth - 2f * spacingPx).positive()
    val vSpace = (constraints.maxHeight - maxVTextWidth - spacingPx).positive()

    // Compute the column size.
    val cellSize =
        if (players.isEmpty()) 0f else minOf(hSpace / (players.size + 1), vSpace / players.size)

    // Compute the size.
    val width = ((players.size + 1) * cellSize + maxHTextWidth + 2 * spacingPx).positive()
    val height = (players.size * cellSize + maxVTextWidth + spacingPx).positive()

    // Measure all the items, according to the retrieved constraints.
    val hPlayersPlaceables =
        hPlayersMeasurables.map { it.measure(Constraints(maxWidth = maxHTextWidth)) }
    val vPlayersPlaceables =
        vPlayersMeasurables.map { it.measure(Constraints(maxWidth = maxVTextWidth)) }
    val scoreByPlayerPlaceable =
        scoreByPlayerMeasurables.map {
          it.measure(Constraints.fixed(cellSize.roundToInt(), cellSize.roundToInt()))
        }
    val scorePlaceable = scoreMeasurable.measure(Constraints(maxWidth = maxVTextWidth))
    val gridPlaceable =
        gridMeasurable.measure(
            Constraints.fixed(
                (players.size * cellSize).roundToInt(), (players.size * cellSize).roundToInt()))

    // Place everything.
    layout(width.roundToInt(), height.roundToInt()) {
      // Place the horizontal texts.
      hPlayersPlaceables.fastForEachIndexed { i, placeable ->
        val x = 0f
        val y = maxVTextWidth + spacingPx + i * cellSize + (cellSize - placeable.height) / 2f
        placeable.place(x.roundToInt(), y.roundToInt())
      }
      // Place the vertical texts.
      vPlayersPlaceables.fastForEachIndexed { i, placeable ->
        val x = maxHTextWidth + spacingPx + i * cellSize + (cellSize - placeable.width) / 2f
        val y = (placeable.width - placeable.height) / 2f
        placeable.place(x.roundToInt(), y.roundToInt())
      }
      scorePlaceable.place(
          x =
              (maxHTextWidth +
                      2 * spacingPx +
                      cellSize * players.size +
                      (cellSize - scorePlaceable.width) / 2f)
                  .roundToInt(),
          y = ((scorePlaceable.width - scorePlaceable.height) / 2f).roundToInt())
      // Place the grid.
      gridPlaceable.place(
          x = (maxHTextWidth + spacingPx).roundToInt(),
          y = (maxVTextWidth + spacingPx).roundToInt(),
      )
      // TODO : Extract this to its own layout, so the corners can be rounded.
      scoreByPlayerPlaceable.fastForEachIndexed { i, placeable ->
        val x = maxHTextWidth + 2 * spacingPx + gridPlaceable.width
        val y = maxVTextWidth + spacingPx + i * cellSize
        placeable.place(x = x.roundToInt(), y = y.roundToInt())
      }
    }
  }
}

private fun Float.positive() = coerceAtLeast(0f)

/**
 * A custom composable which displays a [Grid] of items, with equal length and height, depending on
 * the provided items. The cell items try to fill the available space and will be colored depending
 * on their position.
 *
 * @param T the type of the items in the grid.
 * @param items the list of items to display.
 * @param modifier the [Modifier] for this composable.
 * @param itemContent the content of each item cell.
 */
@Composable
private fun <T> Grid(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable BoxScope.(from: T, to: T) -> Unit,
) {
  Layout(
      content = {
        items.fastForEachIndexed { x, from ->
          items.fastForEachIndexed { y, to ->
            Cell(background = color(x, y), Modifier.fillMaxSize()) { itemContent(from, to) }
          }
        }
      },
      modifier = modifier,
  ) { measurables, constraints ->
    // Measure at maximum available space, ignoring minimum size constraints.
    val itemSizePx = minOf(constraints.maxWidth, constraints.maxHeight) / items.size.toFloat()
    val itemConstraints = Constraints.fixed(itemSizePx.roundToInt(), itemSizePx.roundToInt())
    val placeables = measurables.fastMap { it.measure(itemConstraints) }
    val width = items.size * itemSizePx
    val height = items.size * itemSizePx
    layout(width.roundToInt(), height.roundToInt()) {
      for (i in items.indices) {
        for (j in items.indices) {
          // Since we control the content that will be displayed and know it stretches, we can
          // simply position the items at the top-left corner of the cell.
          val placeable = placeables[i + j * items.size]
          val x = i * itemSizePx
          val y = j * itemSizePx
          placeable.place(x.roundToInt(), y.roundToInt())
        }
      }
    }
  }
}

/** Returns the color at the cell with position [x]. */
private fun color(x: Int) = if (x % 2 == 0) PawniesColors.Green100 else PawniesColors.Green200

/** Returns the color at the cell with positions [x], [y]. */
private fun color(x: Int, y: Int) = if (x == y) PawniesColors.Orange200 else color(x + y)

/**
 * A single cell within a [PoolTable], which has a content.
 *
 * @param background the [Color] for this cell.
 * @param modifier the [Modifier] for this composable.
 * @param content the content of the cell.
 */
@Composable
private fun Cell(
    background: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
  val color by animateColorAsState(background)
  Box(
      modifier = modifier.defaultMinSize(DefaultCellSize).drawBehind { drawRect(color) },
      contentAlignment = Alignment.Center,
      content = content,
  )
}

/** The default size of a cell. */
private val DefaultCellSize = 20.dp

/** The default grid shape. */
private val DefaultGridShape = RoundedCornerShape(8.dp)

/** The default angle for vertical text rotation. */
private const val DefaultVerticalTextAngle = -90f

// FIXME : REMOVE THESE PREVIEW COMPOSABLES

data class IndexedPoolMember(
    val index: Int,
    override val name: String,
    override val total: PoolScore?,
) : PoolMember

object IndexPoolData : PoolData<IndexedPoolMember> {
  override val members =
      listOf(
          IndexedPoolMember(0, "Alexandre", 2),
          IndexedPoolMember(1, "Badr", 10),
          IndexedPoolMember(2, "Chau", 16),
          IndexedPoolMember(3, "Fouad", 5),
          IndexedPoolMember(4, "Lars", 6),
          IndexedPoolMember(5, "Matthieu", 4),
      )

  override fun IndexedPoolMember.scoreAgainst(other: IndexedPoolMember) =
      ((index + other.index) * 373) % 5
}

@Preview
@Composable
fun WonderfulPreview() = PawniesTheme {
  Box(Modifier.fillMaxSize(), Alignment.Center) { PoolTable(IndexPoolData) }
}
