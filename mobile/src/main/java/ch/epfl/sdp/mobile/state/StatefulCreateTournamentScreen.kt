package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialog
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState.*
import kotlin.math.log2
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A composable that makes a [CreateDialog] stateful.
 *
 * @param user the current [AuthenticatedUser].
 * @param navigateToTournament The action to navigate to a certain tournament via its reference.
 * @param cancelClick The action to take when clicking on the cancel button.
 * @param modifier The [Composable]'s modifier.
 */
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

  val actions =
      rememberUpdatedState(
          CreateTournamentDialogActions(
              navigateToTournament = navigateToTournament,
              cancelClick = cancelClick,
          ),
      )

  val state =
      remember(user, actions, strings, tournamentFacade, scope) {
        ActualCreateTournamentScreenState(
            user = user,
            actions = actions,
            tournamentFacade = tournamentFacade,
            strings = strings,
            scope = scope,
        )
      }
  CreateDialog(
      state = state,
      modifier = modifier,
      poolSizeKey = { it.value },
      eliminationRoundKey = { it.value },
  )
}

/**
 * An implementation of the [CreateDialogState].
 *
 * @property user The current [AuthenticatedUser].
 * @property actions The actions to perform for navigating when interacting with the dialog's
 * buttons.
 * @property tournamentFacade The tournament facade to act on the store.
 * @property strings The current [LocalizedStrings].
 * @property scope The [Composable]'s [CoroutineScope].
 */
class ActualCreateTournamentScreenState(
    val user: AuthenticatedUser,
    val actions: State<CreateTournamentDialogActions>,
    val tournamentFacade: TournamentFacade,
    val strings: LocalizedStrings,
    val scope: CoroutineScope,
) : CreateDialogState<IntChoice, IntChoice> {

  override var name: String by mutableStateOf("")

  override fun onBestOfClick(count: Int) {
    bestOf = count
  }
  override val bestOfChoices: List<Int> = listOf(1, 3, 5)
  override var bestOf: Int? by mutableStateOf(null)
    private set

  override var maximumPlayerCount: String by mutableStateOf("")

  override fun onPoolSizeClick(poolSize: IntChoice) {
    this.poolSize = poolSize
  }
  override val poolSizeChoices: List<IntChoice>
    get() =
        listOf(IntChoice(name = strings.tournamentsCreateQualifierSize0, value = 0)) +
            (2..(maximumPlayerCount.toIntOrNull() ?: 0)).map {
              IntChoice(name = strings.tournamentsCreateQualifierSizeN(it), value = it)
            }

  override var poolSize: IntChoice? by mutableStateOf(null)
    private set

  override fun onEliminationRoundClick(eliminationRound: IntChoice) {
    this.eliminationRound = eliminationRound
  }
  override val eliminationRoundChoices: List<IntChoice>
    get() {
      val players = (maximumPlayerCount.toIntOrNull() ?: 0) / 2
      val depth = log2(players.toDouble()).toInt()

      fun intPow(base: Int, exponent: Int): Int {
        return base.toDouble().pow(exponent).toInt()
      }

      return listOf(IntChoice(name = strings.tournamentsCreateElimDepthFinal, value = 1)) +
          (1..depth).map {
            IntChoice(
                name = strings.tournamentsCreateElimDemomN(intPow(2, it)),
                value = it + 1,
            )
          }
    }
  override var eliminationRound: IntChoice? by mutableStateOf(null)
    private set

  override val confirmEnabled: Boolean
    get() =
        tournamentFacade.validParameters(
            name = name,
            bestOf = bestOf,
            maximumPlayerCount = maximumPlayerCount.toIntOrNull(),
            poolSize = poolSize?.value,
            eliminationRounds = eliminationRound?.value,
        )

  override fun onConfirm() {
    scope.launch {
      val reference =
          tournamentFacade.createTournament(
              user = user,
              name = name,
              maxPlayers = maximumPlayerCount.toInt(),
              bestOf = bestOf ?: 1,
              poolSize = poolSize?.value ?: 0,
              eliminationRounds = eliminationRound?.value ?: 1,
          )
      if (reference != null) {
        actions.value.navigateToTournament(reference)
      }
    }
  }
  override fun onCancel() = actions.value.cancelClick()
}

/**
 * A data class that contains [CreateDialog] actions.
 *
 * @property navigateToTournament the action to perform to navigate to a certain tournament after
 * clicking the create button.
 * @property cancelClick the action to perform when clicking the cancel buttons.
 */
data class CreateTournamentDialogActions(
    val navigateToTournament: (reference: TournamentReference) -> Unit,
    val cancelClick: () -> Unit,
)

/**
 * Represents a [Choice] with underlying integer values.
 *
 * @property name the name of the choice.
 * @property value the underlying [Int] value.
 */
data class IntChoice(
    override val name: String,
    val value: Int,
) : Choice
