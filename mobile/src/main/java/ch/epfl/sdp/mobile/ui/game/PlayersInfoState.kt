package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

@Stable
interface PlayersInfoState {

  /** An enumeration representing the different messages that may be displayed with a [Player]. */
  enum class Message {
    /** The player has no message. */
    None,
    /** It's this player's turn. */
    YourTurn,
    /** The player is in check. */
    InCheck,
    /** The player has lost by checkmate. */
    Checkmate,
    /** There's a stalemate. */
    Stalemate,
  }

  /**
   * A class representing a [Player] in the game. There are two players in a game, one for each
   * color.
   *
   * @param name the name of the player, or null if the name is still loading.
   * @param message the message next to the player.
   */
  data class Player(val name: String?, val message: Message)

  /** The information about the white player. */
  val white: Player

  /** The information about the black player. */
  val black: Player
}
