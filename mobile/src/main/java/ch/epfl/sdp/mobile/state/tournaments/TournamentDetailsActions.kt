package ch.epfl.sdp.mobile.state.tournaments

/**
 * A data class which represents some common actions that might be performed on the tournament
 * details screen.
 *
 * @param onBackClick a callback which is called when the user wants to go back.
 */
data class TournamentDetailsActions(
    val onBackClick: () -> Unit,
)
