@file:OptIn(ExperimentalPagerApi::class)

package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.TournamentDetailsClose
import ch.epfl.sdp.mobile.ui.plus
import ch.epfl.sdp.mobile.ui.profile.SettingTabItem
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.PoolBanner.*
import ch.epfl.sdp.mobile.ui.tournaments.TournamentMatch.Result
import ch.epfl.sdp.mobile.ui.tournaments.TournamentsFinalsRound.Banner
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

  /** The result of this [TournamentMatch]. */
  val result: Result

  /** An enumeration with the possible values of a [TournamentMatch]. */
  enum class Result {
    Ongoing,
    Draw,
    FirstWon,
    SecondWon,
  }
}

/**
 * A class representing the ongoing results of a round of the tournament.
 *
 * @param M the type of the [TournamentMatch]es.
 */
@Stable
interface TournamentsFinalsRound<M : TournamentMatch> {

  /** The name of this round. */
  val name: String

  /** The [List] of matches to display. */
  val matches: List<M>

  /** The [Banner] to display. */
  val banner: Banner?

  /** A callback which is called when the banner is clicked. */
  fun onBannerClick()

  /** A [Banner] which represents the content to display. */
  sealed interface Banner {

    /**
     * The banner to move to the next best of round.
     *
     * @param round the next best of.
     * @param total the total best ofs.
     */
    data class NextBestOf(val round: Int, val total: Int) : Banner

    /** The banner to move to the next elimination round. */
    object NextRound : Banner
  }
}

/**
 * The state which will be used to hoist a [TournamentDetails] screen.
 *
 * @param P the type of the [PoolMember]s.
 * @param M the type of the [TournamentMatch]es.
 */
@Stable
interface TournamentDetailsState<P : PoolMember, M : TournamentMatch> {

  /** The current badge to display, if any. */
  val badge: BadgeType?

  /** The title of this tournament. */
  val title: String

  /** The list of [PoolInfo] to be displayed in this first tab. */
  val pools: List<PoolInfo<P>>

  /** The list of [TournamentsFinalsRound] to be displayed in supplementary tabs. */
  val finals: List<TournamentsFinalsRound<M>>

  /** The banner to display at the top of the pools tab. */
  enum class PoolBanner {

    /** The banner if there are enough not players to start the tournament. */
    NotEnoughPlayers,

    /** The banner if there is enough players to start. */
    EnoughPlayers,

    /** The banner if direct eliminations should be started. */
    StartDirectElimination,
  }

  /** The action to that should be displayed on the pools tab. */
  val poolBanner: PoolBanner?

  /** A callback which is called the pool action is clicked. */
  fun onPoolBannerClick()

  /**
   * A callback which is called when the badge is clicked (typically, if the user wants to join the
   * tournament).
   */
  fun onBadgeClick()

  /**
   * A callback which is called when the user wants to watch a specific match.
   *
   * @param match the match which should be watched.
   */
  fun onWatchMatchClick(match: M)

  /** A callback which is called when the user wants to close the screen. */
  fun onCloseClick()
}

/**
 * A composable which displays all the details about a specific tournament.
 *
 * @param P the type of the [PoolMember]s.
 * @param M the type of the [TournamentMatch]es.
 * @param state the [TournamentDetailsState] which hoists the state of this screen.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
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
            badge = state.badge,
            onClose = state::onCloseClick,
            onBadgeClick = state::onBadgeClick,
            badgeEnabled = state.badge == BadgeType.Join,
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
                pools = state.pools,
                startTournamentBanner = state.poolBanner,
                onStartTournamentClick = state::onPoolBannerClick,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding + paddingValues,
            )
          } else {
            val round = state.finals[index - 1]
            DetailsFinals(
                modifier = Modifier.fillMaxSize(),
                banner = round.banner,
                onBannerClick = round::onBannerClick,
                matches = round.matches,
                contentPadding = contentPadding + paddingValues,
                onWatchClick = state::onWatchMatchClick,
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
 * @param badge the badge of the top bar, if any.
 * @param onClose the callback called when the user presses the back action.
 * @param onBadgeClick the callback called then the user presses the badge icon.
 * @param badgeEnabled true if the badge should be clickable.
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
    badge: BadgeType?,
    onClose: () -> Unit,
    onBadgeClick: () -> Unit,
    badgeEnabled: Boolean,
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
            val strings = LocalLocalizedStrings.current
            IconButton(onClose) {
              Icon(
                  imageVector = PawniesIcons.TournamentDetailsClose,
                  contentDescription = strings.tournamentDetailsBackContentDescription,
              )
            }
            Text(
                text = title.uppercase(),
                maxLines = 1,
                overflow = Ellipsis,
                style = MaterialTheme.typography.h5,
            )
          }
          if (badge != null) {
            Badge(
                type = badge,
                onClick = onBadgeClick,
                modifier = Modifier.padding(start = 24.dp, end = 16.dp),
                enabled = badgeEnabled,
            )
          }
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

/**
 * The screen which will be displayed in the pools tab.
 *
 * @param P the type of the [PoolMember]s.
 * @param pools the [List] of pools to display.
 * @param startTournamentBanner the banner to start the tournament which should be displayed.
 * @param onStartTournamentClick the callback which is called when the start tournament action is
 * clicked.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this tab.
 */
@Composable
private fun <P : PoolMember> DetailsPools(
    pools: List<PoolInfo<P>>,
    startTournamentBanner: TournamentDetailsState.PoolBanner?,
    onStartTournamentClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val strings = LocalLocalizedStrings.current
  LazyColumn(
      modifier = modifier,
      contentPadding = contentPadding + PaddingValues(16.dp),
      verticalArrangement = spacedBy(16.dp),
  ) {
    when (startTournamentBanner) {
      NotEnoughPlayers ->
          item {
            OrangeNextStepBanner(
                title = strings.tournamentsDetailsStartNotEnoughPlayersTitle,
                message = strings.tournamentsDetailsStartNotEnoughPlayersSubtitle,
                onClick = onStartTournamentClick,
            )
          }
      EnoughPlayers ->
          item {
            GreenNextStepBanner(
                title = strings.tournamentsDetailsStartEnoughPlayersTitle,
                message = strings.tournamentsDetailsStartEnoughPlayersSubtitle,
                onClick = onStartTournamentClick,
            )
          }
      StartDirectElimination ->
          item {
            GreenNextStepBanner(
                title = strings.tournamentsDetailsStartDirectEliminationTitle,
                message = strings.tournamentsDetailsStartDirectEliminationSubtitle,
                onClick = onStartTournamentClick,
            )
          }
      else -> Unit
    }
    items(pools) { PoolCard(it) }
  }
}

/**
 * The screen which will be displayed in the finals tab.
 *
 * @param M the type of the [TournamentMatch].
 * @param banner the [TournamentsFinalsRound.Banner] to display, if there's any.
 * @param onBannerClick the callback called when the user presses the banner.
 * @param matches the [List] of matches to display.
 * @param onWatchClick the callback called when the user wants to watch a specific match.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this tab.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun <M : TournamentMatch> DetailsFinals(
    banner: Banner?,
    onBannerClick: () -> Unit,
    matches: List<M>,
    onWatchClick: (M) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val ongoingMatches = remember(matches) { matches.filter { it.result == Result.Ongoing } }
  val doneMatches = remember(matches) { matches.filter { it.result != Result.Ongoing } }
  val strings = LocalLocalizedStrings.current
  LazyColumn(
      modifier = modifier,
      contentPadding = contentPadding,
      verticalArrangement = Top,
  ) {
    if (banner != null) {
      item {
        when (banner) {
          is Banner.NextBestOf ->
              GreenNextStepBanner(
                  title = strings.tournamentsDetailsNextBestOfTitle(banner.round, banner.total),
                  message = strings.tournamentsDetailsNextBestOfSubtitle,
                  onClick = onBannerClick,
                  modifier = Modifier.padding(16.dp),
              )
          Banner.NextRound ->
              OrangeNextStepBanner(
                  title = strings.tournamentsDetailsNextRoundTitle,
                  message = strings.tournamentsDetailsNextRoundSubtitle,
                  onClick = onBannerClick,
                  modifier = Modifier.padding(16.dp),
              )
        }
      }
    }
    if (ongoingMatches.isNotEmpty()) {
      item { DetailsSectionHeader(strings.tournamentsDetailsHeaderOngoing) }
    }
    items(ongoingMatches) {
      DetailsMatch(
          first = it.firstPlayerName,
          second = it.secondPlayerName,
          result = it.result,
          onWatchClick = { onWatchClick(it) },
          modifier = Modifier.fillParentMaxWidth(),
      )
    }
    if (doneMatches.isNotEmpty()) {
      item { DetailsSectionHeader(strings.tournamentsDetailsHeaderDone) }
    }
    items(doneMatches) {
      DetailsMatch(
          first = it.firstPlayerName,
          second = it.secondPlayerName,
          result = it.result,
          onWatchClick = { onWatchClick(it) },
          modifier = Modifier.fillParentMaxWidth(),
      )
    }
  }
}

/**
 * A header for a section of items.
 *
 * @param text the title of the header.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun DetailsSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
  Text(
      text = text,
      style = MaterialTheme.typography.h6,
      color = PawniesColors.Green200,
      modifier = modifier.padding(16.dp),
  )
}

/**
 * A composable which displays the details of a match.
 *
 * @param first the name of the first player.
 * @param second the name of the second player
 * @param result the [Result] of the match
 * @param onWatchClick the callback called if the [Result] is [Result.Ongoing] and the action is
 * clicked.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun DetailsMatch(
    first: String,
    second: String,
    result: Result,
    onWatchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  ProvideTextStyle(MaterialTheme.typography.subtitle1) {
    Row(modifier.padding(16.dp), SpaceBetween, CenterVertically) {
      Column(Modifier, spacedBy(4.dp)) {
        Text(first.uppercase())
        Text(second.uppercase())
      }
      val strings = LocalLocalizedStrings.current
      AnimatedContent(result) {
        when (it) {
          Result.Ongoing ->
              OutlinedButton(
                  onClick = onWatchClick,
                  shape = CircleShape,
              ) { Text(strings.tournamentsDetailsWatch) }
          Result.Draw -> Text(strings.tournamentsDetailsMatchDrawn, color = PawniesColors.Green200)
          Result.FirstWon ->
              Column(Modifier, spacedBy(4.dp), End) {
                Text(strings.tournamentsDetailsMatchWon, color = PawniesColors.Green200)
                Text(strings.tournamentsDetailsMatchLost, color = PawniesColors.Orange200)
              }
          Result.SecondWon ->
              Column(Modifier, spacedBy(4.dp), End) {
                Text(strings.tournamentsDetailsMatchLost, color = PawniesColors.Orange200)
                Text(strings.tournamentsDetailsMatchWon, color = PawniesColors.Green200)
              }
        }
      }
    }
  }
}
