package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.state.Loadable.Companion.loaded
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.MatchResult
import ch.epfl.sdp.mobile.ui.social.Win
import kotlinx.coroutines.flow.*

/**
 * Player class that holds info about chess player
 * @param newGame Callable when new game button is actioned. Switches to pregame screen
 */
/* FIXME: add more useful args : user... */
class Player(private val newGame: () -> Unit) : PlayScreenState {
  /* TODO: override other attributes */
  override val onNewGameClick = newGame
}

sealed interface Loadable< out T> {
  object Loading : Loadable<Nothing>
  data class Loaded<out T>(val value: T) : Loadable<T>

  companion object {
    fun loading(): Loadable<Nothing> = Loading
    fun <T> loaded(value: T): Loadable<T> = Loaded(value)
  }
}

/**
 * A stateful implementation of the PlayScreen
 * @param navigateToGame Callable lambda to navigate to game screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulPlayScreen(
    navigateToGame: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {

  val state = remember(navigateToGame) { Player(newGame = navigateToGame) }
  PlayScreen(state, modifier, contentPadding)
}

fun fetchMatches(match: Match, user: AuthenticatedUser): Flow<Loadable<ChessMatch>> {
  val black = match.black
  val white = match.white
  val game = match.game

  val adversary =
      black
          .flatMapMerge { if (user.uid == it?.uid) black else white }
          .map { it?.name ?: "" }
          .map { loaded(it) }
          .onStart { emit(Loadable.Loading) }
  val movesCount =
      game.map { it.serialize().size }.map { loaded(it) }.onStart { emit(Loadable.Loading) }

  return combine(adversary, movesCount) { a, m ->
    Pair(a, m).
    when {
      a as Loadable.Loaded<String> ->
          loaded(ChessMatch(
              adversary = v, matchResult = Win(MatchResult.Reason.CHECKMATE), numberOfMoves = m))
      else -> Loadable.Loading
    }
  }
}

fun <Loadable<String>,  Loadable<Int>> Pair<*, *>.ChessMatchAdapter(): Pair<Any, Loadable> {

}