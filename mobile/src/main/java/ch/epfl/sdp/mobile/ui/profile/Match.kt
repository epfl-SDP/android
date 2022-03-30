package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.social.Loss
import ch.epfl.sdp.mobile.ui.social.MatchResult
import ch.epfl.sdp.mobile.ui.social.Tie
import ch.epfl.sdp.mobile.ui.social.Win

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
  icon: ImageVector,
  modifier: Modifier = Modifier,
) {
  ListItem(
    modifier = modifier,
    icon = { Icon(icon, null) },
    text = { Text(title) },
    secondaryText = { Text(subtitle) },
  )
}


/**
 * Chooses a subtitle given a [MatchResult] and number of moves [nMoves] of the match
 * @param matchResult result of the match
 * @param nMoves number of moves
 */
fun chooseSubtitle(
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
    }
  return text(nMoves)
}
