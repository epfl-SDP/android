package ch.epfl.sdp.mobile.state.game.core

import ch.epfl.sdp.mobile.application.chess.engine.Game

/** An interface which represents a delegate which has read access to an underlying [Game]. */
interface GameDelegate {

  /** The available [Game]. */
  val game: Game
}
