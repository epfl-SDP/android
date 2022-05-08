package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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

  ListItem(
      modifier = Modifier.clickable { onClick() }.padding(vertical = 12.5.dp),
      text = { Text(contest.name, color = PawniesColors.Green500) },
      secondaryText = {
        Text(
            buildAnnotatedString {
              withStyle(style = SpanStyle(color = PawniesColors.Green200)) {
                append(strings.tournamentsStarted)
              }
              withStyle(style = SpanStyle(color = PawniesColors.Orange200)) {
                append(" ${contest.creationDate} ")
              }
              withStyle(style = SpanStyle(color = PawniesColors.Green200)) {
                append(strings.tournamentsAgo)
              }
            },
            style = MaterialTheme.typography.subtitle2)
      },
      trailing = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          displayBadge(strings, contest, onClick, modifier)
        }
      })
}

@Composable
private fun displayContestStatus(strings: LocalizedStrings, contest: Contest, modifier: Modifier) {
  if (contest.status == ONGOING) {
    Text(
        buildAnnotatedString {
          withStyle(style = SpanStyle(color = PawniesColors.Green200)) {
            append(strings.tournamentsStarted)
          }
          withStyle(style = SpanStyle(color = PawniesColors.Orange200)) {
            append(" ${contest.creationDate} ")
          }
          withStyle(style = SpanStyle(color = PawniesColors.Green200)) {
            append(strings.tournamentsAgo)
          }
        },
        style = MaterialTheme.typography.subtitle2)
  } else {
    Text(
        strings.tournamentsDone,
        color = PawniesColors.Green200,
        style = MaterialTheme.typography.subtitle2,
        modifier = modifier)
  }
}

@Composable
private fun displayBadge(
    strings: LocalizedStrings,
    contest: Contest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  when (contest.personStatus) {
    ADMIN -> Badge(type = BadgeType.Admin, onClick = onClick, modifier.padding(vertical = 12.5.dp))
    PARTICIPANT -> Badge(type = BadgeType.Participant, onClick = onClick, modifier)
    VIEWER -> Badge(type = BadgeType.Join, onClick = onClick, modifier)
  }
}
