package ch.epfl.sdp.mobile.state.tournaments

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade.TournamentFilters
import ch.epfl.sdp.mobile.state.LocalTournamentFacade
import ch.epfl.sdp.mobile.ui.tournaments.FiltersDialog
import ch.epfl.sdp.mobile.ui.tournaments.FiltersDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * A stateful composable which displays the tournaments dialog.
 *
 * @param navigateBack a callback which is called when the user wants to navigate back.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulFiltersDialogScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val currentNavigateBack = rememberUpdatedState(navigateBack)
  val coroutineScope = rememberCoroutineScope()
  val facade = LocalTournamentFacade.current

  val state =
      remember(currentNavigateBack, coroutineScope, facade) {
        ActualFiltersDialogState(
            navigateBack = currentNavigateBack,
            scope = coroutineScope,
            facade = facade,
        )
      }

  FiltersDialog(
      state = state,
      modifier = modifier,
  )
}

/**
 * The actual implementation of a [FiltersDialogState], which delegates calls to the underlying
 * facade.
 *
 * @param navigateBack the callback when the user wants to go back.
 * @param scope the [CoroutineScope] to be used for asynchronous work.
 * @param facade the underlying [TournamentFacade].
 */
private class ActualFiltersDialogState(
    navigateBack: State<() -> Unit>,
    private val scope: CoroutineScope,
    facade: TournamentFacade,
) : FiltersDialogState {

  /** The current callback to navigate back. */
  private val navigateBack by navigateBack

  /** The currently set [TournamentFilters]. */
  private var filters by
      mutableStateOf(
          TournamentFilters(
              showDone = false,
              showParticipating = false,
              showAdministrating = false,
              facade = facade,
          ),
      )

  init {
    scope.launch { facade.filters().onEach { filters = it }.collect() }
  }

  override val onlyShowDone: Boolean
    get() = filters.showDone

  override val onlyShowParticipating: Boolean
    get() = filters.showParticipating

  override val onlyShowAdministrating: Boolean
    get() = filters.showAdministrating

  override fun onShowDoneClick() {
    scope.launch { filters.update { onlyShowDone(!onlyShowDone) } }
  }

  override fun onShowParticipatingClick() {
    scope.launch { filters.update { onlyShowParticipating(!onlyShowParticipating) } }
  }

  override fun onShowAdministratingClick() {
    scope.launch { filters.update { onlyShowAdministrating(!onlyShowAdministrating) } }
  }

  override fun onBack() = navigateBack()
}
