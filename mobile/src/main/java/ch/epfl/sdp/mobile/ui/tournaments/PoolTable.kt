package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.FadeEnterTransition
import ch.epfl.sdp.mobile.ui.FadeExitTransition
import ch.epfl.sdp.mobile.ui.PawniesColors
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
  val strings = LocalLocalizedStrings.current
  val fadeTransition: AnimatedContentScope<*>.() -> ContentTransform = {
    FadeEnterTransition with FadeExitTransition
  }
  ProvideTextStyle(textStyle) {
    CompositionLocalProvider(LocalContentColor provides PawniesColors.Green800) {
      PoolContent(
          players = data.members,
          modifier = modifier,
          playerContent = {
            AnimatedContent(
                targetState = it.name.uppercase(),
                transitionSpec = fadeTransition,
                contentAlignment = Alignment.Center,
            ) { text ->
              Text(
                  text = text,
                  modifier = Modifier.widthIn(max = 200.dp),
                  overflow = TextOverflow.Ellipsis,
              )
            }
          },
          scoreTitleContent = {
            Text(
                text = strings.tournamentsTableScore.uppercase(),
                modifier = Modifier.widthIn(max = 200.dp),
                overflow = TextOverflow.Ellipsis,
            )
          },
          scoreContent = {
            AnimatedContent(
                targetState = it.total,
                transitionSpec = fadeTransition,
                contentAlignment = Alignment.Center,
            ) { total -> total?.let { score -> Text(score.toString()) } }
          },
      ) { from, to ->
        AnimatedContent(
            targetState = with(data) { from.scoreAgainst(to) },
            transitionSpec = fadeTransition,
            contentAlignment = Alignment.Center,
        ) { score -> score?.let { Text(it.toString()) } }
      }
    }
  }
}

/**
 * A custom layout which places around the different pieces of the [PoolTable] composable.
 *
 * @param T the type of items which are displayed.
 * @param players the list of the players to display.
 * @param modifier the [Modifier] for this composable.
 * @param spacing the spacing between the items of the layout.
 * @param playerContent the body of a player name composable.
 * @param scoreTitleContent the body of the score title composable.
 * @param scoreContent the body of the total score cell composable.
 * @param itemContent the body of a grid item.
 */
@Composable
private fun <T : PoolMember> PoolContent(
    players: List<T>,
    modifier: Modifier = Modifier,
    spacing: Dp = 16.dp,
    playerContent: @Composable (T) -> Unit,
    scoreTitleContent: @Composable () -> Unit,
    scoreContent: @Composable BoxScope.(T) -> Unit,
    itemContent: @Composable BoxScope.(from: T, to: T) -> Unit,
) {
  Layout(
      content = {
        // Compose all the players (horizontally)
        players.fastForEach { player -> Box(Modifier) { playerContent(player) } }
        // Compose all the players (vertically)
        players.fastForEach { player ->
          Box(Modifier.rotate(DefaultVerticalTextAngle)) { playerContent(player) }
        }
        // Compose the score results
        Column(players, Modifier.clip(DefaultGridShape), scoreContent)
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
    val scoreByPlayerMeasurable = measurables[2 * players.size]
    val scoreMeasurable = measurables[2 * players.size + 1]
    val gridMeasurable = measurables[2 * players.size + 2]
    val spacingPx = spacing.toPx()

    // Compute the max intrinsic width of the texts.
    val maxHTextIntrinsicWidth =
        hPlayersMeasurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 0
    val scoreWidth = scoreMeasurable.maxIntrinsicWidth(constraints.maxHeight)
    val maxVTextIntrinsicWidth =
        maxOf(
            scoreWidth,
            vPlayersMeasurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) }
                ?: scoreWidth,
        )

    // Measure the players texts.
    val hPlayersPlaceables =
        hPlayersMeasurables.map { it.measure(Constraints(maxWidth = maxHTextIntrinsicWidth)) }
    val vPlayersPlaceables =
        vPlayersMeasurables.map { it.measure(Constraints(maxWidth = maxVTextIntrinsicWidth)) }

    // Use the actual maximum and minimum size measurement to compute the grid size.
    val maxHTextWidth = hPlayersPlaceables.maxOfOrNull { it.width } ?: 0
    val maxVTextWidth = vPlayersPlaceables.maxOfOrNull { it.width } ?: 0

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
    val scoreByPlayerPlaceable =
        scoreByPlayerMeasurable.measure(Constraints(maxWidth = cellSize.roundToInt()))
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
      // Place the scores.
      scoreByPlayerPlaceable.place(
          x = (maxHTextWidth + 2 * spacingPx + gridPlaceable.width).roundToInt(),
          y = (maxVTextWidth + spacingPx).roundToInt(),
      )
    }
  }
}

/** Returns the receiver [Float] with a minimum value of 0 (included). */
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
    val itemSizePx =
        if (items.isEmpty()) 0f
        else minOf(constraints.maxWidth, constraints.maxHeight) / items.size.toFloat()
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

/**
 * A custom composable which displays a [Column] of items, depending on the provided width of the
 * column. The cell items try to fill the available space and will be colored depending on their
 * position.
 *
 * @param T the type of the items in the column.
 * @param items the list of items to display.
 * @param modifier the [Modifier] for this composable.
 * @param itemContent the content of each item cell.
 */
@Composable
private fun <T> Column(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
  Layout(
      content = {
        items.fastForEachIndexed { index, item ->
          Cell(background = color(index), Modifier.fillMaxSize()) { itemContent(item) }
        }
      },
      modifier = modifier,
  ) { measurables, constraints ->
    // Measurable at maximum available space.
    val itemSizePx = if (items.isEmpty()) 0 else constraints.maxWidth
    val itemConstraints = Constraints.fixed(itemSizePx, itemSizePx)
    val placeables = measurables.fastMap { it.measure(itemConstraints) }
    layout(itemSizePx, items.size * itemSizePx) {
      placeables.fastForEachIndexed { index, placeable ->
        val x = 0
        val y = index * itemSizePx
        placeable.place(x, y)
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
