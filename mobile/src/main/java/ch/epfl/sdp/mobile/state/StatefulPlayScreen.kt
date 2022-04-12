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

/**
 * A stateful implementation of the PlayScreen
 * @param user the Authenticated user
 * @param onGameItemClick callback function to navigate to game on click
 * @param navigateToPrepareGame Callable lambda to navigate to [PrepareGameScreen] screen
 * @param navigateToLocalGame Callable lambda to navigate to a certain local game screen
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding The [PaddingValues] to apply to the [PlayScreen]
 */
@Composable
fun StatefulPlayScreen(
    user: AuthenticatedUser,
    onGameItemClick: (ChessMatchAdapter) -> Unit,
    navigateToPrepareGame: () -> Unit,
    navigateToLocalGame: (match: Match) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chess = LocalChessFacade.current
  val currentNavigateToPrepareGame = rememberUpdatedState(navigateToPrepareGame)
  val currentNavigateToLocalGame = rememberUpdatedState(navigateToLocalGame)
  val onGameItemClickAction = rememberUpdatedState(onGameItemClick)
  val scope = rememberCoroutineScope()
  val state =
      remember(
          user,
          navigateToLocalGame,
          navigateToPrepareGame,
          chess,
          scope,
      ) {
        PlayScreenStateImpl(
            user = user,
            onLocalGameClickAction = currentNavigateToLocalGame,
            onOnlineGameClickAction = currentNavigateToPrepareGame,
            onMatchClickAction = onGameItemClickAction,
            chessFacade = chess,
            scope = scope,
        )
      }
  PlayScreen(state = state, modifier = modifier, key = { it.uid }, contentPadding = contentPadding)
}

/** An Loadable Union Type to differentiate between loaded and state */
private sealed interface Loadable<out T> {
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
private fun <A, B> Loadable<A>.map(f: (A) -> B): Loadable<B> =
    when (this) {
      is Loadable.Loaded -> loaded(f(value))
      Loadable.Loading -> loading()
    }

/**
 * Extract the value out of the Loadable type
 * @param lazyBlock function which returns alternative value
 */
private fun <A> Loadable<A>.orElse(lazyBlock: () -> A): A =
    when (this) {
      is Loadable.Loaded -> value
      Loadable.Loading -> lazyBlock()
    }

/**
 * Gives higher order function to decide the MatchResult
 * @param userColor color of the current user
 */
private fun NextStep.toMatchResult(): (userColor: Color) -> MatchResult =
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

/** An adapter that is of type [ChessMatch] and contains the uid. */
data class ChessMatchAdapter(
    val uid: String,
    override val adversary: String,
    override val matchResult: MatchResult,
    override val numberOfMoves: Int
) : ChessMatch {}

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
 * Implementation of the PlayScreenState
 * @param onNewGameClick callback function for the new game button
 * @param user authenticated user
 * @param chessFacade [ChessFacade] to fetch the matches
 * @param scope for coroutines
 */
private class PlayScreenStateImpl(
    override val user: AuthenticatedUser,
    onLocalGameClickAction: State<(match: Match) -> Unit>,
    onOnlineGameClickAction: State<() -> Unit>,
    onMatchClickAction: State<(ChessMatchAdapter) -> Unit>,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : PlayScreenState<ChessMatchAdapter> {
  val onMatchClickAction by onMatchClickAction
  val onLocalGameClickAction by onLocalGameClickAction
  val onOnlineClickAction by onOnlineGameClickAction

  override var matches by mutableStateOf(emptyList<ChessMatchAdapter>())
    private set

  init {
    scope.launch {
      fetchForUser(user, chessFacade).collect { list ->
        matches = list.map { createChessMatch(it, user) }
      }
    }
  }

  override fun onMatchClick(match: ChessMatchAdapter) {
    onMatchClickAction(match)
  }

  override fun onLocalGameClick() {
    scope.launch {
      val match = chessFacade.createLocalMatch(user)
      onLocalGameClickAction(match)
    }
  }
  override fun onOnlineGameClick() = this.onOnlineClickAction()
}

/**
 * Helper function for createChessMatch
 * @param match [MatchInfo] intermediate datatype
 * @param user authenticated user
 */
private fun createChessMatch(match: MatchInfo, user: AuthenticatedUser): ChessMatchAdapter =
    ChessMatchAdapter(
        adversary = if (user.uid == match.blackId) match.whiteName else match.blackName,
        matchResult =
            match.matchResult(if (user.uid == match.blackId) Color.Black else Color.White),
        numberOfMoves = match.movesCount,
        uid = match.id ?: "")
