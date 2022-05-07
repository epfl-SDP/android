package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.PawniesTheme

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

  // TODO : Admin stuff, to start the next round.
}

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
    Column(Modifier.padding(16.dp), spacedBy(16.dp)) {
      Text("Hello there my title whatever") // TODO : Custom
      Divider() // TODO : Custom
      PoolTable(info)
    }
  }
}

// FIXME : REMOVE THESE PREVIEW COMPOSABLES

data class IndexedPoolMember(
    val index: Int,
    override val name: String,
    override val total: PoolScore?,
) : PoolMember

class IndexPoolInfo(private val index: Int) : PoolInfo<IndexedPoolMember> {
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

  override val name: String = "Pool #$index"

  override val status: PoolInfo.Status = PoolInfo.Status.StillOpen
}

@Preview
@Composable
fun WonderfulPreview() = PawniesTheme {
  LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = spacedBy(16.dp),
      contentPadding = PaddingValues(16.dp),
  ) { items(100) { PoolCard(IndexPoolInfo(it)) } }
}
