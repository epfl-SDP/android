package ch.epfl.sdp.mobile.state.game.core

import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action

/** A refinement of [GameDelegate] which has write access to the underlying [Game]. */
interface MutableGameDelegate : GameDelegate {

  /**
   * Tries to perform the given [Action] of the [Game], updating it on success.
   *
   * @param action the [Action] which is attempted.
   * @return true if the [Action] was performed on the [Game].
   */
  fun tryPerformAction(action: Action): Boolean
}
