package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of the [ProfileScreenState] that performs a given profile's [ChessMatch]
 * requests.
 *
 * @param user the given [Profile].
 * @param actions the [ProfileActions] which are available on the screen.
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class StatefulProfileScreen(
    user: Profile,
    actions: State<ProfileActions>,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : ProfileScreenState<ChessMatchAdapter, PuzzleInfoAdapter>, Person by ProfileAdapter(user) {
  private val actions by actions

  override var matches by mutableStateOf(emptyList<ChessMatchAdapter>())
    private set

  override val pastGamesCount
    get() = matches.size

  override fun onMatchClick(match: ChessMatchAdapter) = actions.onMatchClick(match)

  override var puzzles by mutableStateOf(emptyList<PuzzleInfoAdapter>())
    private set

  override val solvedPuzzlesCount
    get() = puzzles.size

  override fun onPuzzleClick(puzzle: PuzzleInfoAdapter) = actions.onPuzzleClick(puzzle)

  init {
    scope.launch {
      fetchForUser(user, chessFacade).collect { list ->
        matches = list.map { createChessMatch(it, user) }
      }
    }

    scope.launch {
      puzzles = chessFacade.solvedPuzzles(user).map { it.toPuzzleInfoAdapter() }.sortedBy { it.elo }
    }
  }
}

/** An interface representing the different actions available on the profile and settings screen. */
interface ProfileActions {

  /* Callback function fro a clicked match */
  val onMatchClick: (ChessMatchAdapter) -> Unit

  /* Callback function called when a puzzle is clicked on.*/
  val onPuzzleClick: (PuzzleInfoAdapter) -> Unit
}
