package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.ContestPersonStatus.*
import ch.epfl.sdp.mobile.ui.tournaments.ContestStatus.*

/** Composes a Contest log. */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContestItem(contest: Contest, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  val title = DisplayContestStatus(strings, contest)

  ListItem(
      modifier = Modifier.clickable { onClick() },
      text = { Text(contest.name, color = PawniesColors.Green500) },
      secondaryText = {
        Text(
            text = title,
            color = PawniesColors.Green200,
            style = MaterialTheme.typography.subtitle2,
            modifier = modifier)
      },
      trailing = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          DisplayBadge(contest.personStatus, onClick, modifier)
        }
      })
}

@Composable
private fun DisplayContestStatus(strings: LocalizedStrings, contest: Contest): AnnotatedString {
  return if (contest.status == ONGOING) {
    buildAnnotatedString {
      append(strings.tournamentsStarted)
      withStyle(style = SpanStyle(color = PawniesColors.Orange200)) {
        append(" ${contest.creationDate.absoluteValue} ")
      }
      append(strings.tournamentsAgo)
    }
  } else {
    AnnotatedString(strings.tournamentsDone)
  }
}

@Composable
private fun DisplayBadge(
    personStatus: ContestPersonStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  when (personStatus) {
    ADMIN -> Badge(type = BadgeType.Admin, onClick = onClick, modifier = modifier)
    PARTICIPANT -> Badge(type = BadgeType.Participant, onClick = onClick, modifier = modifier)
    VIEWER -> Badge(type = BadgeType.Join, onClick = onClick, modifier = modifier)
  }
}
