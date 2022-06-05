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
import androidx.compose.ui.text.style.TextOverflow
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo.Status

/**
 * Displays a contest.
 *
 * @param contestInfo the given [ContestInfo].
 * @param onClick callback function called when the contest item is clicked on.
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Contest(
    contestInfo: ContestInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val title =
      when (val status = contestInfo.status) {
        Status.Done -> AnnotatedString(strings.tournamentsDone)
        is Status.Ongoing ->
            strings.tournamentsStartingTime(
                status.since,
                SpanStyle(color = PawniesColors.Orange200),
            )
      }
  ListItem(
      modifier = modifier.clickable { onClick() },
      text = {
        Text(
            text = contestInfo.name,
            color = PawniesColors.Green500,
            style = MaterialTheme.typography.subtitle1,
        )
      },
      secondaryText = {
        Text(
            text = title,
            color = PawniesColors.Green200,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
      },
      trailing = {
        val badge = contestInfo.badge
        if (badge != null) {
          Badge(type = badge, enabled = false, onClick = {})
        }
      },
  )
}
