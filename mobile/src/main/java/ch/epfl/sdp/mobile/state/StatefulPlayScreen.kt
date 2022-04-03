package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.state.Loadable.Companion.loaded
import ch.epfl.sdp.mobile.state.Loadable.Companion.loading
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Tie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** An Loadable Union Type to differentiate between loaded and state */
sealed interface Loadable<out T> {
  object Loading : Loadable<Nothing>
  data class Loaded<out T>(val value: T) : Loadable<T>

  companion object {
    fun loading(): Loadable<Nothing> = Loading
    fun <T> loaded(value: T): Loadable<T> = Loaded(value)
  }
}
/**
 * A map for the Loadable type
 * @param f callback function for map
 */
private inline fun <A, B> Loadable<A>.map(f: (A) -> B): Loadable<B> =
    when (this) {
      is Loadable.Loaded -> loaded(f(value))
      Loadable.Loading -> loading()
    }

/**
 * Extract the value out of the Loadable type
 * @param lazyBlock function which returns alternative value
 */
private inline fun <A> Loadable<A>.orElse(lazyBlock: () -> A): A =
    when (this) {
      is Loadable.Loaded -> value
      Loadable.Loading -> lazyBlock()
    }

/** Intermediate State to simplify the data handling */
private data class MatchInfo(
    val id: String?,
    val movesCount: Int,
    val whiteId: String,
    val blackId: String,
    val whiteName: String,
    val blackName: String,
)

/**
 * Fetches the matches from the facade and convert it to a list of MatchInfos
 * @param currentUser the current authenticated user
 * @param facade the faced we fetch the matches
 */
private fun fetchForUser(
    currentUser: AuthenticatedUser,
    facade: ChessFacade,
): Flow<List<MatchInfo>> =
    facade.matches(currentUser).map { m -> m.map { info(it) } }.flatMapLatest { matches ->
      combine(matches) { it.toList() }
    }

/**
 * Extract the data from the subflows of black and white and combine all the information in
 * MatchInfo
 * @param match a [Match] to extract the informations
 */
private fun info(match: Match): Flow<MatchInfo> {
  val black = match.black
  val white = match.white
  val game = match.game

  val blackName = black.map { loaded(it) }.onStart { emit(loading()) }
  val whiteName = white.map { loaded(it) }.onStart { emit(loading()) }
  val loadingGame = game.map { loaded(it) }.onStart { emit(loading()) }

  return combine(blackName, whiteName, loadingGame) { b, w, g ->
    MatchInfo(
        id = match.id,
        movesCount = g.map { it.serialize().size }.orElse { 0 },
        whiteId = w.map { it?.uid ?: "" }.orElse { "" },
        blackId = w.map { it?.uid ?: "" }.orElse { "" },
        whiteName = w.map { it?.name ?: "" }.orElse { "" },
        blackName = b.map { it?.name ?: "" }.orElse { "" },
    )
  }
}

/**
 * Implementation of the PlayscreenState
 * @param onNewGameClick callback function for the new game button
 * @param user authenticated user
 * @param facade [ChessFacade] to fetch the matches
 * @param scope for coroutines
 */
private class PlayScreenStateImpl(
    onNewGameClick: State<() -> Unit>,
    user: AuthenticatedUser,
    facade: ChessFacade,
    scope: CoroutineScope,
) : PlayScreenState {
  override val onNewGameClick by onNewGameClick
  override var matches by mutableStateOf(emptyList<ChessMatch>())
    private set

  init {
    scope.launch {
      fetchForUser(user, facade).collect { list ->
        matches =
            list.map {
              ChessMatch(
                  adversary = if (user.uid == it.blackId) it.blackName else it.whiteName,
                  matchResult = Tie,
                  numberOfMoves = it.movesCount,
              )
            }
      }
    }
  }
}

/**
 * A stateful implementation of the PlayScreen
 * @param user the Authenticated user
 * @param navigateToGame Callable lambda to navigate to game screen
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun StatefulPlayScreen(
    user: AuthenticatedUser,
    navigateToGame: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val facade = LocalChessFacade.current
  val currentNavigateToGame = rememberUpdatedState(navigateToGame)
  val scope = rememberCoroutineScope()
  val state =
      remember(user, facade, scope) {
        PlayScreenStateImpl(
            onNewGameClick = currentNavigateToGame,
            user = user,
            facade = facade,
            scope = scope,
        )
      }
  PlayScreen(state, modifier, contentPadding)
}
