package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialog
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState.*
import kotlinx.coroutines.CoroutineScope

@Composable
fun StatefulCreateTournamentScreen(
    user: AuthenticatedUser,
    navigateToTournament: () -> Unit,
    cancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  val tournamentFacade = LocalTournamentFacade.current

  val state =
      remember(user, navigateToTournament, cancelClick, scope, tournamentFacade) {
        ActualCreateTournamentScreenState(
            user = user,
            navigateToTournament = navigateToTournament,
            cancelClick = cancelClick,
            tournamentFacade = tournamentFacade,
            scope = scope,
        )
      }
  CreateDialog(
      state = state,
      modifier = modifier,
      poolSizeKey = { TODO() },
      eliminationRoundKey = { TODO() },
  )
}

class ActualCreateTournamentScreenState(
    val user: AuthenticatedUser,
    val navigateToTournament: () -> Unit,
    val cancelClick: () -> Unit,
    val tournamentFacade: TournamentFacade,
    val scope: CoroutineScope,
) : CreateDialogState<Choice, Choice> {
  override var name: String = ""
  override val bestOfChoices: List<Int> = listOf()
  override var bestOf: Int? = null

  override fun onBestOfClick(count: Int) {}

  override var maximumPlayerCount: String = ""
  override val poolSizeChoices: List<Choice> = listOf()

  override fun onPoolSizeClick(poolSize: Choice) {}

  override val eliminationRoundChoices: List<Choice> = listOf()
  override val eliminationRound: Choice? = null

  override fun onEliminationRoundClick(eliminationRound: Choice) {}

  override val confirmEnabled: Boolean = false

  override fun onConfirm() = navigateToTournament()

  override fun onCancel() = cancelClick()

  override val poolSize: Choice? = null
}

data class ActualChoice(
    override val name: String,
) : Choice
