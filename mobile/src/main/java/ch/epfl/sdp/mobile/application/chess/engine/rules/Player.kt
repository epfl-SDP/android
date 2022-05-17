package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote

/**
 * An interface representing a player which can be used to normalize players.
 *
 * @param P the type of the [Player].
 */
interface Player<P : Player<P>> {

  /** Normalizes the given [Player] to a role. */
  fun normalize(player: P): Role = if (player == this) Role.Allied else Role.Adversary

  /** Denormalizes the given [Role] to a [P]layer. */
  fun denormalize(role: Role): P = if (role == Role.Allied) other().other() else other()

  /**
   * Normalizes the given [Position].
   *
   * @param position the [Position] to normalize.
   * @return the resulting [Position].
   */
  fun normalize(position: Position): Position

  /**
   * Denormalizes the given [Position].
   *
   * @param position the [Position] to denormalize.
   * @return the resulting [Position].
   */
  fun denormalize(position: Position): Position

  /**
   * Normalizes the given [Delta].
   *
   * @param delta the [Delta] to normalize.
   * @return the resulting [Delta].
   */
  fun normalize(delta: Delta): Delta

  /**
   * Denormalizes the given [Delta].
   *
   * @param delta the [Delta] to denormalize.
   * @return the resulting [Delta].
   */
  fun denormalize(delta: Delta): Delta

  /** Returns the opposite player. */
  fun other(): P
}

/**
 * Normalizes the given [Action].
 *
 * @receiver the [Player] used for normalization.
 * @param action the [Action] to normalize.
 * @return the resulting [Action].
 */
@JvmName("normalizeAction")
fun Player<*>.normalize(action: Action): Action =
    when (action) {
      is Move -> Move(from = normalize(action.from), delta = normalize(action.delta))
      is Promote ->
          Promote(
              from = normalize(action.from),
              delta = normalize(action.delta),
              rank = action.rank,
          )
    }

/**
 * Denormalizes the given [Action].
 *
 * @receiver the [Player] used for denormalization.
 * @param action the [Action] to denormalize.
 * @return the resulting [Action].
 */
@JvmName("denormalizeAction")
fun Player<*>.denormalize(action: Action): Action =
    when (action) {
      is Move -> Move(from = denormalize(action.from), delta = denormalize(action.delta))
      is Promote ->
          Promote(
              from = denormalize(action.from),
              delta = denormalize(action.delta),
              rank = action.rank,
          )
    }

/**
 * Normalizes the given [Piece].
 *
 * @param P the normalization color.
 * @receiver the [Player] used for normalization.
 * @param piece the [Piece] to normalize.
 * @return the normalized [Piece].
 */
@JvmName("normalizePiece")
fun <P : Player<P>> P.normalize(piece: Piece<P>): Piece<Role> =
    Piece(color = normalize(piece.color), rank = piece.rank, id = piece.id)

/**
 * Denormalizes the given [Piece].
 *
 * @param P the denormalization color.
 * @receiver the [Player] used for denormalization.
 * @param piece the [Piece] to denormalize.
 * @return the normalized [Piece].
 */
@JvmName("denormalizePiece")
fun <P : Player<P>> P.denormalize(piece: Piece<Role>): Piece<P> =
    Piece(color = denormalize(piece.color), rank = piece.rank, id = piece.id)

// TODO : Effect normalization (if needed)

/**
 * Denormalizes the given [Effect].
 *
 * @param P the denormalization color.
 * @receiver the [Player] used for denormalization.
 * @param effect the [Effect] to denormalize.
 * @return the denormalized [Effect].
 */
@JvmName("denormalizeEffect")
fun <P : Player<P>> P.denormalize(effect: Effect<Piece<Role>>): Effect<Piece<P>> =
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
