package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.text.isDigitsOnly
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialog
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun StatefulCreateTournamentScreen(
    user: AuthenticatedUser,
    navigateToTournament: (reference: TournamentReference) -> Unit,
    cancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()

  val tournamentFacade = LocalTournamentFacade.current
  val strings = LocalLocalizedStrings.current

  val state =
      remember(user, navigateToTournament, cancelClick, scope, tournamentFacade) {
        ActualCreateTournamentScreenState(
            user = user,
            navigateToTournament = navigateToTournament,
            cancelClick = cancelClick,
            tournamentFacade = tournamentFacade,
            strings = strings,
            scope = scope,
        )
      }
  CreateDialog(
      state = state,
      modifier = modifier,
      poolSizeKey = { it.name },
      eliminationRoundKey = { it.name },
  )
}

class ActualCreateTournamentScreenState(
    val user: AuthenticatedUser,
    val navigateToTournament: (reference: TournamentReference) -> Unit,
    val cancelClick: () -> Unit,
    val tournamentFacade: TournamentFacade,
    val strings: LocalizedStrings,
    val scope: CoroutineScope,
) : CreateDialogState<Choice, Choice> {

  override var name: String by mutableStateOf("")

  override fun onBestOfClick(count: Int) {
    bestOf = count
  }
  override val bestOfChoices: List<Int> = listOf(1, 3, 5)
  override var bestOf: Int? by mutableStateOf(null)

  override var maximumPlayerCount: String by mutableStateOf("")

  override fun onPoolSizeClick(poolSize: Choice) {
    this.poolSize = poolSize
  }
  override val poolSizeChoices: List<Choice> =
      listOf(IntChoice(name = strings.tournamentCreateQualifierSize0)) +
          (2..7).map { IntChoice(name = strings.tournamentCreateQualifierSizeN(it.toString())) }

  override var poolSize: Choice? by mutableStateOf(null)

  override fun onEliminationRoundClick(eliminationRound: Choice) {
    this.eliminationRound = eliminationRound
  }
  override val eliminationRoundChoices: List<Choice> =
      listOf(IntChoice(name = strings.tournamentCreateElimDepthFinal)) +
          listOf(2, 4, 8, 16, 32, 64).map {
            IntChoice(name = strings.tournamentCreateElimDepthN(it.toString()))
          }
  override var eliminationRound: Choice? by mutableStateOf(null)

  override val confirmEnabled: Boolean
    get() =
        name.isNotBlank() &&
            bestOf != null &&
            maximumPlayerCount.isDigitsOnly() &&
            poolSize != null &&
            eliminationRound != null

  override fun onConfirm(): Unit = run {
    scope.launch {
      val reference =
          tournamentFacade.createTournament(
              user = user,
              name = name,
              maxPlayers = maximumPlayerCount.toInt(),
              bestOf = bestOf ?: 1,
              poolSize = poolSize?.name?.toIntOrNull() ?: 0,
              eliminationRounds = eliminationRound?.name?.toIntOrNull() ?: 1,
          )
      navigateToTournament(reference)
    }
  }
  override fun onCancel() = cancelClick()
}

data class IntChoice(
    override val name: String,
) : Choice
