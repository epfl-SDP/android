package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.WhiteKing
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.social.*

/**
 * A Slot Component for Profile or Setting Screen
 * @param header part of slot construct that comes into the header
 * @param profileBar part of slot construct that represents the tab bar.
 * @param matches the part that is responsible for the list of all matches
 * @param lazyColumnState to keep state of LazyColumn onMatchClick a callback called when a
 * [ChessMatch] is clicked
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserScreen(
    header: @Composable () -> Unit,
    profileTabBar: @Composable () -> Unit,
    matches: List<ChessMatch>,
    onMatchClick: (ChessMatch) -> Unit,
    lazyColumnState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val strings = LocalLocalizedStrings.current

  LazyColumn(
      state = lazyColumnState,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
      contentPadding = contentPadding,
  ) {
    item { header() }
    stickyHeader { profileTabBar() }
    items(matches) { match ->
      val title = strings.profileMatchTitle(match.adversary)
      val subtitle = chooseSubtitle(strings, match.matchResult, match.numberOfMoves)
      Match(title, subtitle, ChessIcons.WhiteKing)
    }
  }
}

/**
 * Composes a Match log using a match [title], [subtitle] and an [icon]
 * @param title match title
 * @param subtitle match subtitle info
 * @param icon match icon
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Match(
    title: String,
    subtitle: String,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
  val titles = title.split(' ', limit = 2)
  val profileAgainst = titles[0]
  val profileOpponent = titles[1]
  ListItem(
      modifier = modifier,
      icon = { Icon(icon, null, modifier = Modifier.size(40.dp)) },
      text = {
        Text(
            buildAnnotatedString {
              withStyle(style = SpanStyle(color = PawniesColors.Green800)) {
                append("$profileAgainst ")
              }
              withStyle(style = SpanStyle(color = PawniesColors.Green500, fontWeight = SemiBold)) {
                append(profileOpponent)
              }
            },
            style = MaterialTheme.typography.body1)
      },
      secondaryText = { Text(subtitle) },
  )
}

/**
 * Chooses a subtitle given a [MatchResult] and number of moves [nMoves] of the match
 * @param matchResult result of the match
 * @param nMoves number of moves
 */
private fun chooseSubtitle(
    strings: LocalizedStrings,
    matchResult: MatchResult,
    nMoves: Int
): String {
  val text =
      when (matchResult) {
        Tie -> strings.profileTieInfo
        is Loss ->
            when (matchResult.reason) {
              MatchResult.Reason.CHECKMATE -> strings.profileLostByCheckmate
              MatchResult.Reason.FORFEIT -> strings.profileLostByForfeit
            }
        is Win ->
            when (matchResult.reason) {
              MatchResult.Reason.CHECKMATE -> strings.profileWonByCheckmate
              MatchResult.Reason.FORFEIT -> strings.profileWonByForfeit
            }
        else -> strings.profileTieInfo
      }
  return text(nMoves)
}
