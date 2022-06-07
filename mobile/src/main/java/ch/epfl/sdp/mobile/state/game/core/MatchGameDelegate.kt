package ch.epfl.sdp.mobile.state.game.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of [MutableGameDelegate] which writes its updates to a [Match].
 *
 * @property match the underlying [Match].
 * @property scope the [CoroutineScope] used to read and write the match updates.
 */
class MatchGameDelegate(
    private val match: Match,
    private val scope: CoroutineScope,
) : MutableGameDelegate {

  /** The underlying snapshot-aware [Game]. */
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

  override fun tryPerformAction(action: Action): Boolean {
    val step = game.nextStep as? NextStep.MovePiece ?: return false
    if (action !in game.actions(action.from)) return false
    game = step.move(action)
    return true
  }
}
