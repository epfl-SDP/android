@file:OptIn(ExperimentalPagerApi::class)

package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.TournamentDetailsClose
import ch.epfl.sdp.mobile.ui.plus
import ch.epfl.sdp.mobile.ui.profile.SettingTabItem
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/** An interface which represents a match between two players, which both have a name. */
@Stable
interface TournamentMatch {

  /** The name of the first player. */
  val firstPlayerName: String

  /** The name of the second player. */
  val secondPlayerName: String
}

@Stable
data class TournamentsFinalsRound<M : TournamentMatch>(
    val name: String,
    val matches: List<M>,
)

@Stable
interface TournamentDetailsState<P : PoolMember, M : TournamentMatch> {

  val badge: BadgeType?

  val title: String

  val pools: List<PoolInfo<P>>

  val finals: List<TournamentsFinalsRound<M>>

  fun onBadgeClick()

  fun onWatchMatchClick(match: M)

  fun onCloseClick()
}

@Composable
fun <P : PoolMember, M : TournamentMatch> TournamentDetails(
    state: TournamentDetailsState<P, M>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val pagerState = rememberPagerState()
  val coroutineScope = rememberCoroutineScope()
  val strings = LocalLocalizedStrings.current
  val sectionCount = state.finals.size + 1
  Scaffold(
      modifier = modifier,
      topBar = {
        DetailsTopBar(
            title = state.title,
            onClose = state::onCloseClick,
            count = sectionCount,
            selected = pagerState.currentPage,
            sectionTitle = {
              when (it) {
                0 -> strings.tournamentsDetailsPools
                else -> strings.tournamentsDetailsFinals
              }
            },
            sectionSubtitle = {
              when (it) {
                0 -> state.pools.size.toString()
                else -> state.finals[it - 1].name
              }
            },
            onSectionClick = { coroutineScope.launch { pagerState.animateScrollToPage(it) } },
        )
      },
      content = { paddingValues ->
        HorizontalPager(
            state = pagerState,
            count = sectionCount,
            modifier = Modifier.fillMaxSize(),
        ) { index ->
          if (index == 0) {
            DetailsPools(
                modifier = Modifier.fillMaxSize(),
                pools = state.pools,
                contentPadding = contentPadding + paddingValues,
            )
          } else {
            DetailsFinals(
                modifier = Modifier.fillMaxSize(),
                matches = state.finals[index - 1].matches,
                contentPadding = contentPadding + paddingValues,
            )
          }
        }
      },
  )
}

/**
 * The top app bar, which will be displayed on the tournaments screen.
 *
 * @param title the title of the top bar.
 * @param onClose the callback called when the user presses the back action.
 * @param count the number of sections.
 * @param selected the index of the currently selected section.
 * @param sectionTitle returns the title for the n-th section.
 * @param sectionSubtitle returns the subtitle for the n-th section.
 * @param onSectionClick called when the i-th section is pressed.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun DetailsTopBar(
    title: String,
    onClose: () -> Unit,
    count: Int,
    selected: Int,
    sectionTitle: (Int) -> String,
    sectionSubtitle: (Int) -> String,
    onSectionClick: (Int) -> Unit,
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
            selectedTabIndex = selected,
            backgroundColor = MaterialTheme.colors.background,
            indicator = {}, // Hide the default indicator.
            edgePadding = 0.dp, // No start padding.
        ) {
          for (index in 0 until count) {
            SettingTabItem(
                title = sectionTitle(index),
                subtitle = sectionSubtitle(index),
                onClick = { onSectionClick(index) },
                selected = index == selected,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun <P : PoolMember> DetailsPools(
    pools: List<PoolInfo<P>>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  LazyColumn(
      modifier = modifier,
      contentPadding = contentPadding + PaddingValues(16.dp),
      verticalArrangement = spacedBy(16.dp),
  ) { items(pools) { PoolCard(it) } }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun <M : TournamentMatch> DetailsFinals(
    matches: List<M>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  LazyColumn(
      modifier = modifier,
      contentPadding = contentPadding,
      verticalArrangement = Top,
  ) {
    items(matches) {
      DetailsMatch(
          first = it.firstPlayerName,
          second = it.secondPlayerName,
      )
    }
  }
}

@Composable
private fun DetailsMatch(
    first: String,
    second: String,
    modifier: Modifier = Modifier,
) {
  Column(modifier) {
    Text(first)
    Text(second)
  }
}
