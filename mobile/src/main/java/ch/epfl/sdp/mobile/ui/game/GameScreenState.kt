package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * A state which indicates the content of a [GameScreen] composable. It will keep track of the
 * values of moves history.
 *
 * @param Piece the type of the pieces of the underlying [ChessBoardState].
 */
@Stable
interface GameScreenState<Piece : ChessBoardState.Piece> : ChessBoardState<Piece> {

  /**
   * A class representing a [Move] that's been performed by one of the players.
   *
   * @param text the [String] representation of the [Move].
   */
  data class Move(val text: String)

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

  /** A callback which will be invoked when the user clicks on the AR button. */
  fun onArClick()

  /** A [Boolean] which indicates if the device is currently listening to voice inputs. */
  val listening: Boolean

  /** A callback which will be invoked when the user clicks on the listening button. */
  fun onListenClick()

  /**
   * A [List] of all the moves which have been performed by the user. Moves are ordered and should
   * be displayed as such.
   */
  val moves: List<Move>

  /** A callback which will be invoked if the user wants to go back in the hierarchy. */
  fun onBackClick()
}
