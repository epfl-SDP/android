package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesTheme
import ch.epfl.sdp.mobile.ui.tournaments.PoolInfo.Status
import ch.epfl.sdp.mobile.ui.tournaments.PoolInfo.Status.Ongoing
import ch.epfl.sdp.mobile.ui.tournaments.PoolInfo.Status.StillOpen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The information displayed within a pool card.
 *
 * @param P the type of the actual [PoolMember]s.
 * @see PoolData the interface for actual pool details.
 */
@Stable
interface PoolInfo<P : PoolMember> : PoolData<P> {

  /** An interface representing the possible status for a pool. */
  sealed interface Status {

    /** Indicates that the pool has not started yet, and players may still join or leave. */
    object StillOpen : Status

    /**
     * Indicates that the pool is underway, and that [currentRound] matches out of [totalRounds]
     * have already been played.
     */
    data class Ongoing(val currentRound: Int, val totalRounds: Int) : Status
  }

  /** The name of this pool. */
  val name: String

  /** The current status of this pool. */
  val status: Status

  /** True if the button to start the next round should be enabled. */
  val startNextRoundEnabled: Boolean

  /** A callback called when the start next round action is called. */
  fun onStartNextRound()
}

/**
 * Displays a pool and its information.
 *
 * @param P the type of the actual [PoolMember]s.
 * @param info the [PoolInfo] which should be displayed in the cards.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <P : PoolMember> PoolCard(
    info: PoolInfo<P>,
    modifier: Modifier = Modifier,
) {
  Card(
      modifier = modifier,
      shape = RoundedCornerShape(8.dp),
      elevation = 2.dp,
  ) {
    Column(Modifier.padding(16.dp), Top, CenterHorizontally) {
      Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
        PoolNameText(info.name)
        PoolStatusText(info.status)
      }
      AnimatedVisibility(info.startNextRoundEnabled) {
        OutlinedButton(
            onClick = info::onStartNextRound,
            modifier = Modifier.padding(top = 16.dp),
            shape = CircleShape,
        ) { Text(LocalLocalizedStrings.current.tournamentsPoolStartNextRound) }
      }
      DashedDivider(Modifier.padding(top = 16.dp))
      PoolTable(info, Modifier.padding(top = 16.dp))
    }
  }
}

/**
 * Displays the name of the pool.
 *
 * @param name the name of the pool.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun PoolNameText(
    name: String,
    modifier: Modifier = Modifier,
) {
  Text(
      text = name,
      modifier = modifier,
      style = MaterialTheme.typography.subtitle1,
      color = PawniesColors.Green200,
  )
}

/**
 * Displays the color of the pool.
 *
 * @param status the [PoolInfo.Status] for the pool.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun PoolStatusText(
    status: Status,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val text =
      when (status) {
        StillOpen -> strings.tournamentsPoolStillOpen
        is Ongoing -> strings.tournamentsPoolRound(status.currentRound, status.currentRound)
      }
  Text(
      text = text,
      modifier = modifier,
      style = MaterialTheme.typography.subtitle1,
      color = PawniesColors.Orange200,
  )
}

// FIXME : REMOVE THESE PREVIEW COMPOSABLES

data class IndexedPoolMember(
    val index: Int,
    private val nameState: State<String>,
    override val total: PoolScore?,
) : PoolMember {

  constructor(
      index: Int,
      name: String,
      total: PoolScore?,
  ) : this(index, mutableStateOf(name), total)

  override val name by nameState
}

class IndexPoolInfo(
    private val index: Int,
    scope: CoroutineScope,
) : PoolInfo<IndexedPoolMember> {

  private var matthieu = mutableStateOf("Matthieu")

  init {
    scope.launch {
      while (true) {

        matthieu.value = "Matthieu Burguburu The Overflow"
        delay(3000)

        matthieu.value = "Matthieu"
        delay(3000)
      }
    }
  }

  override val members =
      listOf(
          IndexedPoolMember(0, "Alexandre", 2),
          IndexedPoolMember(1, "Badr", 10),
          IndexedPoolMember(2, "Chau", 16),
          IndexedPoolMember(3, "Fouad", 5),
          IndexedPoolMember(4, "Lars", 6),
          IndexedPoolMember(5, matthieu, 4),
      )

  override fun IndexedPoolMember.scoreAgainst(other: IndexedPoolMember) =
      ((index + other.index) * 373) % 5

  override val name: String = "Pool #$index"

  override val status: Status = StillOpen

  override var startNextRoundEnabled by mutableStateOf((index % 2) == 0)
    private set

  override fun onStartNextRound() {
    startNextRoundEnabled = false
  }
}

@Preview
@Composable
fun WonderfulPreview() = PawniesTheme {
  val scope = rememberCoroutineScope()
  LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = spacedBy(16.dp),
      contentPadding = PaddingValues(16.dp),
  ) { items(100) { PoolCard(IndexPoolInfo(it, scope)) } }
}
