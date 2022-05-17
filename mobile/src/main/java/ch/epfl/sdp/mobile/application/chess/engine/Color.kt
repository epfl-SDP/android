package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
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
  fun normalize(action: Action): Action =
      when (action) {
        is Move -> Move(normalize(action.from), normalize(action.delta))
        is Promote -> Promote(normalize(action.from), normalize(action.delta), action.rank)
      }

  /**
   * Denormalizes the given [Effect].
   *
   * @param effect the [Effect] to denormalize.
   * @return the denormalized [Effect].
   */
  fun denormalize(effect: Effect<Piece<Role>>): Effect<Piece<Color>> =
      when (effect) {
        is Effect.Combine -> Effect.Combine(effect.effects.map { denormalize(it) })
        is Effect.Move -> Effect.Move(normalize(effect.from), normalize(effect.to))
        is Effect.Set ->
            Effect.Set(
                normalize(effect.position),
                effect.piece?.let { it ->
                  val color =
                      when (it.color) {
                        Role.Allied -> this
                        Role.Adversary -> other()
                      }
                  Piece(color, it.rank, it.id)
                },
            )
      }

  /** Returns the [Color] of the adversary. */
  fun other(): Color = opposite()
}
