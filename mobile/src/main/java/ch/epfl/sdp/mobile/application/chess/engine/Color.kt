package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.implementation.DenormalizedBoardDecorator
import ch.epfl.sdp.mobile.application.chess.engine.implementation.NormalizedBoardDecorator
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Effect
import ch.epfl.sdp.mobile.application.chess.engine.rules.Role

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
) {

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

  /**
   * Normalizes the given [Delta].
   *
   * @param delta the [Delta] to normalize.
   * @return the normalized delta.
   */
  fun normalize(delta: Delta): Delta = normalizeDelta(delta)

  /**
   * Normalizes the given [Position].
   *
   * @param position the [Position] to normalize.
   * @return the normalized position.
   */
  fun normalize(position: Position): Position = normalizePosition(position)

  /**
   * Normalizes the given [Action].
   *
   * @param action the [Action] to normalize.
   * @return the normalized action.
   */
  fun normalize(action: Action): Action = Action(normalize(action.from), normalize(action.delta))

  // TODO : Is there another way to do this ?
  fun denormalize(effect: Effect<Piece<Role>>): Effect<Piece<Color>> = Effect { board ->
    DenormalizedBoardDecorator(this, effect.perform(NormalizedBoardDecorator(this, board)))
  }

  /** Returns the [Color] of the adversary. */
  fun other(): Color = opposite()
}
