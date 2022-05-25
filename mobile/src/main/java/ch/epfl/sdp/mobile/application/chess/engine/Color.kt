package ch.epfl.sdp.mobile.application.chess.engine

/**
 * The classical colors of a game of chess.
 *
 * @param opposite a function that returns the opposite color.
 */
enum class Color(private val opposite: () -> Color) {

  /** The black color. */
  Black(opposite = { White }),

  /** The white color. Starts the game. */
  White(opposite = { Black });

  /** Returns the [Color] of the adversary. */
  fun other(): Color = opposite()
}
