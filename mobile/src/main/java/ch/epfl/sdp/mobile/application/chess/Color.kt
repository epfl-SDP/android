package ch.epfl.sdp.mobile.application.chess

/**
 * The classical colors of a game of chess.
 *
 * @param normalizeToWhiteDelta a function which normalizes the [Delta].
 * @param normalizeToWhitePosition a function which normalizes the [Position].
 * @param opposite a function that returns the opposite color.
 */
enum class Color(
    private val normalizeToWhiteDelta: Delta.() -> Delta,
    private val normalizeToWhitePosition: Position.() -> Position,
    private val opposite: () -> Color,
) {

  /** The black color. */
  Black(
      normalizeToWhiteDelta = { Delta(x = x, y = -y) },
      normalizeToWhitePosition = {
        Position(
            x = x,
            y = Board.Size - y - 1, // A simple vertical symmetry.
        )
      },
      opposite = { White },
  ),

  /** The white color. Starts the game. */
  White(
      normalizeToWhiteDelta = { this },
      normalizeToWhitePosition = { this },
      opposite = { Black },
  );

  /**
   * Normalizes the given [Delta].
   *
   * @param delta the [Delta] to normalize.
   * @return the normalized delta.
   */
  fun normalize(delta: Delta): Delta = normalizeToWhiteDelta(delta)

  /**
   * Normalizes the given [Position].
   *
   * @param position the [Position] to normalize.
   * @return the normalized position.
   */
  fun normalize(position: Position): Position = normalizeToWhitePosition(position)

  /** Returns the [Color] of the adversary. */
  fun other(): Color = opposite()
}
