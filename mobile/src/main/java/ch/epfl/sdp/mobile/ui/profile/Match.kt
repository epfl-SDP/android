package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.social.*

/**
 * Composes a Match log using a match [adversary], [subtitle] and an [icon].
 * @param match the [ChessMatch] to compose.
 * @param icon match icon.
 * @param onClick callback function if clicked on the item.
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Match(
    match: ChessMatch,
    icon: Painter,
    onClick: (ChessMatch) -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val subtitle = chooseSubtitle(strings, match.matchResult, match.numberOfMoves)

  ListItem(
      modifier = modifier.clickable { onClick(match) },
      icon = { Icon(icon, null, modifier = Modifier.size(40.dp)) },
      text = {
        Text(
            buildAnnotatedString {
              withStyle(style = SpanStyle(color = PawniesColors.Green800)) {
                append(LocalLocalizedStrings.current.profileAgainst)
              }
              withStyle(
                  style =
                      SpanStyle(color = PawniesColors.Green500, fontWeight = FontWeight.SemiBold)) {
                append(match.adversary)
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
