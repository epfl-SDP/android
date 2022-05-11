package ch.epfl.sdp.mobile.state.game.core

import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action

/** A refinement of [GameDelegate] which has write access to the underlying [Game]. */
interface MutableGameDelegate : GameDelegate {

  override var game: Game

  /**
   * Tries to perform the given [Action] of the [Game], updating it on success.
   *
   * @param action the [Action] which is attempted.
   * @return true if the [Action] was performed on the [Game].
   */
  fun tryPerformAction(action: Action): Boolean {
    val step = game.nextStep as? NextStep.MovePiece ?: return false
    if (action !in game.actions(action.from)) return false
    game = step.move(action)
    return true
  }
}
