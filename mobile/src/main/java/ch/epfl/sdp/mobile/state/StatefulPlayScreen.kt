package ch.epfl.sdp.mobile.state

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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


sealed interface Loadable<out T> {
  object Loading : Loadable<Nothing>
  data class Loaded<out T>(val value: T) : Loadable<T>

  companion object {
    fun loading(): Loadable<Nothing> = Loading
    fun <T> loaded(value: T): Loadable<T> = Loaded(value)
  }
}

data class PlayScreenStateImpl(
  override val onNewGameClick: () -> Unit,
  override val matches: Loadable<List<ChessMatch>>
) : PlayScreenState

/**
 * A stateful implementation of the PlayScreen
 * @param navigateToGame Callable lambda to navigate to game screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulPlayScreen(
  user: AuthenticatedUser,
  navigateToGame: () -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(),
) {
  val chessFacade = LocalChessFacade.current
  val matches =
    remember(user) {
      chessFacade.matches(user).map {
        it.map { match -> matchToChessMatch(match, user) }
      }.flatMapLatest { matches -> combine(matches) { it.toList() } }
        .map {
          it.foldRight(loaded(emptyList<ChessMatch>())) { elem, acc ->
            when (acc) {
              is Loadable.Loading -> Loadable.Loading
              is Loadable.Loaded<*> ->
                when (elem) {
                  is Loadable.Loading -> Loadable.Loading
                  is Loadable.Loaded<*> -> {
                    val newList: List<ChessMatch> = (acc.value as List<ChessMatch>).plus(elem.value as ChessMatch)
                    loaded(newList)
                  }
                }
            }
          }
        }
    }
      .collectAsState(Loadable.Loading).value

  val state = remember(navigateToGame) {
    PlayScreenStateImpl(
      onNewGameClick = navigateToGame,
      matches = matches
    )
  }
  PlayScreen(state, modifier, contentPadding)
}

fun matchToChessMatch(match: Match, user: AuthenticatedUser): Flow<Loadable<ChessMatch>> {
  val black = match.black
  val white = match.white
  val game = match.game

  val adversary =
    black
      .flatMapMerge { if (user.uid == it?.uid) white else black }
      .map { it?.name ?: "" }
      .map { loaded(it) }
      .onStart { emit(Loadable.Loading) }
  val movesCount =
    game.map { it.serialize().size }.map { loaded(it) }.onStart { emit(Loadable.Loading) }

  return combine(adversary, movesCount) { (a, m) ->
    when (a) {
      Loadable.Loading -> Loadable.Loading
      is Loadable.Loaded<*> ->
        when (m) {
          is Loadable.Loading -> Loadable.Loading
          is Loadable.Loaded<*> ->
            loaded(
              ChessMatch(
                adversary = a.value as String,
                matchResult = Win(MatchResult.Reason.CHECKMATE),
                numberOfMoves = m.value as Int,
              )
            )

        }
    }
  }
}
