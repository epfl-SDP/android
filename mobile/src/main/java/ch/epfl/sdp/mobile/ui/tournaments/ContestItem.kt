package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.Status.ContestStatus.*

/**
 * Displays a contest.
 *
 * @param contest the given [Contest].
 * @param onClick callback function called when the contest item is clicked on.
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContestItem(
    contest: Contest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val title = status(strings, contest)
  ListItem(
      modifier = modifier.clickable { onClick() },
      text = {
        Text(
            text = contest.name,
            color = PawniesColors.Green500,
            style = MaterialTheme.typography.subtitle1,
        )
      },
      secondaryText = {
        Text(
            text = title,
            color = PawniesColors.Green200,
            style = MaterialTheme.typography.subtitle2,
        )
      },
      trailing = { Badge(type = contest.personStatus, onClick = onClick) },
  )
}

/**
 * Displays the bottom text part containing the status the contest item.
 *
 * @param strings the given [LocalizedStrings].
 * @param contest the given [Contest].
 */
private fun status(strings: LocalizedStrings, contest: Contest): AnnotatedString {
  return if (contest.status == ONGOING) {
    strings.tournamentsStartingTime(
        contest.creationTime,
        SpanStyle(color = PawniesColors.Orange200),
    )
  } else {
    AnnotatedString(strings.tournamentsDone)
  }
}
