@file:OptIn(ExperimentalPagerApi::class)

package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.PawniesTheme
import ch.epfl.sdp.mobile.ui.TournamentDetailsClose
import ch.epfl.sdp.mobile.ui.plus
import ch.epfl.sdp.mobile.ui.profile.SettingTabItem
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.CoroutineScope

@Composable
fun <Section> TournamentDetails(
    sections: List<Section>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    section: @Composable Section.(PaddingValues) -> Unit
) {
  Scaffold(
      modifier = modifier,
      topBar = {
        DetailsTopBar(
            title = "EPFL Masters Chess",
            onClose = {},
        )
      },
      content = { paddingValues ->
        HorizontalPager(
            count = sections.size,
            modifier = Modifier.fillMaxSize(),
        ) { section(sections[it], contentPadding + paddingValues) }
      },
  )
}

@Composable
private fun DetailsTopBar(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      color = MaterialTheme.colors.background,
      elevation = AppBarDefaults.TopAppBarElevation,
      modifier = modifier,
  ) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
      Column {
        // Top part of the bar.
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically,
        ) {
          Row(Modifier.weight(1f, fill = true), spacedBy(16.dp), CenterVertically) {
            IconButton(onClose) { Icon(PawniesIcons.TournamentDetailsClose, null) }
            Text(
                text = title.uppercase(),
                maxLines = 1,
                overflow = Ellipsis,
                style = MaterialTheme.typography.h5,
            )
          }
          Badge(BadgeType.Join, onClick = {}, Modifier.padding(start = 24.dp, end = 16.dp))
        }
        // Tabs.
        ScrollableTabRow(
            selectedTabIndex = 0,
            backgroundColor = MaterialTheme.colors.background,
            indicator = {}, // Hide the default indicator.
            edgePadding = 0.dp, // No start padding.
        ) {
          repeat(10) {
            SettingTabItem(
                title = "Hello",
                subtitle = "World",
                onClick = {},
                selected = it == 0,
            )
          }
        }
      }
    }
  }
}

// FIXME : REMOVE THESE PREVIEW COMPOSABLES

@Preview
@Composable
private fun TournamentsPreview() = PawniesTheme {
  val scope = rememberCoroutineScope()
  TournamentDetails(
      sections = listOf(1, 2, 3),
      modifier = Modifier.fillMaxSize(),
  ) { paddingValues ->
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues + PaddingValues(16.dp),
        verticalArrangement = spacedBy(16.dp),
    ) { items(100) { PoolCard(IndexPoolInfo(it, scope)) } }
  }
}

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
    // scope.launch {
    //   while (true) {
    //
    //     matthieu.value = "Matthieu Burguburu The Overflow"
    //     delay(3000)
    //
    //     matthieu.value = "Matthieu"
    //     delay(3000)
    //   }
    // }
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

  override val status: PoolInfo.Status = PoolInfo.Status.StillOpen

  override var startNextRoundEnabled by mutableStateOf((index % 2) == 0)
    private set

  override fun onStartNextRound() {
    startNextRoundEnabled = false
  }
}

// @Stable
// interface TournamentsDetailsState<P : PoolMember, Pool : PoolInfo<P>, M : TournamentMatch> {
//
//   @Stable
//   interface Section<P : PoolMember, Pool : PoolInfo<P>, M : TournamentMatch> {
//     data class Pools<P : PoolMember, Pool : PoolInfo<P>>(val pools: List<Pool>) : Section<P,
// Pool, *>
//     data class Matches
//   }
//
//   val badge: BadgeType?
//
//   val sections: List<Section>
//
//   // TODO : Callbacks.
//   fun onWatchMatchClick(match: M)
// }
//
// @Stable
// interface TournamentMatch {
//   val firstPlayerName: String
//   val secondPlayerName: String
// }
//
// @Composable
// fun <P : PoolMember, Pool : PoolInfo<P>, Match : TournamentMatch> TournamentDetails(
//     state: TournamentsDetailsState<P, Pool, Match>,
//     onCloseClick: () -> Unit,
//     modifier: Modifier = Modifier,
//     contentPadding: PaddingValues = PaddingValues(),
// ) {
//   HorizontalPager(
//       count = state.sections.size,
//   ) {}
// }
