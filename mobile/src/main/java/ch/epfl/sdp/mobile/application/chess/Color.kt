package ch.epfl.sdp.mobile.application.chess

/**
 * The classical colors of a game of chess.
 *
 * @param normalizeToWhite a function which normalizes the [Position].
 * @param opposite a function that returns the opposite color.
 */
enum class Color(
    private val normalizeToWhite: Position.() -> Position,
    private val opposite: () -> Color,
) {

  /** The black color. */
  Black(
      normalizeToWhite = {
        Position(
            x = x,
            y = Board.Size - y - 1, // A simple vertical symmetry.
        )
      },
      opposite = { White },
  ),

  /** The white color. Starts the game. */
  White(
      normalizeToWhite = { this },
      opposite = { Black },
  );

  /**
   * Normalizes the given [Position].
   *
   * @param position the [Position] to normalize.
   * @return the normalized position.
   */
  fun normalize(position: Position): Position = normalizeToWhite(position)

  /** Returns the [Color] of the adversary. */
  fun other(): Color = opposite()
}
