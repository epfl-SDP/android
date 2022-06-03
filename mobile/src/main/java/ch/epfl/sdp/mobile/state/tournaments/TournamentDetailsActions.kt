package ch.epfl.sdp.mobile.state.tournaments

/**
 * A data class which represents some common actions that might be performed on the tournament
 * details screen.
 *
 * @property onBackClick a callback which is called when the user wants to go back.
 * @property onMatchClick a callback which is called when the user wants to show a match.
 */
data class TournamentDetailsActions(
    val onBackClick: () -> Unit,
    val onMatchClick: (String) -> Unit,
)
