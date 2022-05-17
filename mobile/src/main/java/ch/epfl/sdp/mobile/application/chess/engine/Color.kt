package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.rules.Player

/**
 * The classical colors of a game of chess.
 *
 * @param normalizeDelta a function which normalizes the [Delta].
 * @param normalizePosition a function which normalizes the [Position].
 * @param opposite a function that returns the opposite color.
 */
enum class Color(
    private val normalizeDelta: Delta.() -> Delta,
    private val normalizePosition: Position.() -> Position,
    private val opposite: () -> Color,
) : Player<Color> {

  /** The black color. */
  Black(
      normalizeDelta = { Delta(x = x, y = -y) },
      normalizePosition = {
        Position(
            x = x,
            y = Board.Size - y - 1, // A simple vertical symmetry.
        )
      },
      opposite = { White },
  ),

  /** The white color. Starts the game. */
  White(
      normalizeDelta = { this },
      normalizePosition = { this },
      opposite = { Black },
  );

  override fun denormalize(delta: Delta): Delta = normalizeDelta(delta)
  override fun denormalize(position: Position): Position = normalizePosition(position)
  override fun normalize(delta: Delta): Delta = normalizeDelta(delta)
  override fun normalize(position: Position): Position = normalizePosition(position)
  override fun other(): Color = opposite()
}
