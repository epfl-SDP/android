package ch.epfl.sdp.mobile.state.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A [ChessBoardState] which uses a [Match] to implement the display of pieces through
 * [ChessBoardState].
 *
 * @param match the [Match] which should be displayed.
 * @param scope the [CoroutineScope] in which the match is observed.
 */
class MatchChessBoardState(
    private val match: Match,
    private val scope: CoroutineScope,
) : GameChessBoardState {

  private var backing by mutableStateOf(Game.create())

  override var game: Game
    get() = backing
    set(value) {
      backing = value // Local updates
      scope.launch { match.update(value) }
    }

  init {
    scope.launch { match.game.collect { backing = it } }
  }
}
