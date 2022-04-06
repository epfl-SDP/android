package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.SectionSocial
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.social.*

/**
 * Composes a Match log using a match [title], [subtitle] and an [icon]
 * @param match a chess Match
 * @param onMatchClick callback function if clicked on item
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Match(
    match: ChessMatch,
    onMatchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val title = strings.profileMatchTitle(match.adversary)
  val subtitle = chooseSubtitle(strings, match.matchResult, match.numberOfMoves)

  ListItem(
      modifier = modifier.clickable { onMatchClick() },
      icon = { Icon(PawniesIcons.SectionSocial, null) },
      text = { Text(title) },
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
        is YourTurn -> { _ -> strings.profileYourTurn }
        is OtherTurn -> { _ -> strings.profileOthersTurn }
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
      }

  return text(nMoves)
}
