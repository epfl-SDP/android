package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

@Stable
interface MovesInfoState {

  /**
   * A class representing a [Move] that's been performed by one of the players.
   *
   * @param text the [String] representation of the [Move].
   */
  data class Move(val text: String)

  /**
   * A [List] of all the moves which have been performed by the user. Moves are ordered and should
   * be displayed as such.
   */
  val moves: List<Move>
}
