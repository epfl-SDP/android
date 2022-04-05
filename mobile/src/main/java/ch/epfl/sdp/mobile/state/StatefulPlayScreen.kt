package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.state.Loadable.Companion.loaded
import ch.epfl.sdp.mobile.state.Loadable.Companion.loading
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState
import ch.epfl.sdp.mobile.ui.social.*
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

/**
 * Gives higher order function to decide the MatchResult
 * @param userColor color of the current user
 */
private inline fun NextStep.toMatchResult(): (userColor: Color) -> MatchResult =
    when (this) {
      is NextStep.Stalemate -> { _ -> Tie }
      is NextStep.MovePiece -> { userColor -> if (this.turn == userColor) YourTurn else OtherTurn }
      is NextStep.Checkmate -> { userColor ->
            if (userColor == this.winner) {
              Win(MatchResult.Reason.CHECKMATE)
            } else {
              Loss(MatchResult.Reason.CHECKMATE)
            }
          }
    }

/** Intermediate State to simplify the data handling */
private data class MatchInfo(
    val id: String?,
    val movesCount: Int,
    val whiteId: String,
    val blackId: String,
    val whiteName: String,
    val blackName: String,
    val matchResult: (userColor: Color) -> MatchResult
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
    val blackId = b.map { it?.uid ?: "" }.orElse { "" }
    val whiteId = w.map { it?.uid ?: "" }.orElse { "" }
    MatchInfo(
        id = match.id,
        movesCount = g.map { it.serialize().size }.orElse { 0 },
        whiteId = whiteId,
        blackId = blackId,
        whiteName = w.map { it?.name ?: "" }.orElse { "" },
        blackName = b.map { it?.name ?: "" }.orElse { "" },
        matchResult = g.map { it.nextStep.toMatchResult() }.orElse { { Tie } })
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
    override val onGameItemClick: (ChessMatch) -> Unit,
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
        matches = list.map { createChessMatch(it, user) }
      }
    }
  }
}

/**
 * Helper function for createChessMatch
 * @param match [MatchInfo] intermediate datatype
 * @param user authenticated user
 */
private fun createChessMatch(match: MatchInfo, user: AuthenticatedUser): ChessMatch =
    ChessMatch(
        adversary = if (user.uid == match.blackId) match.whiteName else match.blackName,
        matchResult =
            match.matchResult(if (user.uid == match.blackId) Color.Black else Color.White),
        numberOfMoves = match.movesCount,
        uid = match.id ?: "")

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
    onGameItemClick: (ChessMatch) -> Unit,
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
            onGameItemClick = onGameItemClick,
            user = user,
            facade = facade,
            scope = scope,
        )
      }
  PlayScreen(state, modifier, contentPadding)
}
