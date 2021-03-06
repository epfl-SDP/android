package ch.epfl.sdp.mobile.state.tournaments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.state.LocalTournamentFacade
import ch.epfl.sdp.mobile.ui.tournaments.*

/**
 * A stateful screen which displays the details of a tournament.
 *
 * @param user the currently authenticated user.
 * @param reference the [TournamentReference] to the tournament which should be displayed.
 * @param actions the actions which may be performed by the user.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding some [PaddingValues] to apply around the content.
 */
@Composable
fun StatefulTournamentDetailsScreen(
    user: AuthenticatedUser,
    reference: TournamentReference,
    actions: TournamentDetailsActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val currentActions = rememberUpdatedState(actions)
  val coroutineScope = rememberCoroutineScope()
  val facade = LocalTournamentFacade.current
  val state =
      remember(currentActions, user, facade, reference, coroutineScope) {
        ActualTournamentDetailsState(
            actions = currentActions,
            user = user,
            facade = facade,
            reference = reference,
            scope = coroutineScope,
        )
      }
  TournamentDetails(
      state = state,
      modifier = modifier,
      contentPadding = contentPadding,
  )
}
