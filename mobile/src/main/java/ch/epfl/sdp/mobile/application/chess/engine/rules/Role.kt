package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position

/** An enumeration representing the [Role] that each piece may have, from a logical perspective. */
enum class Role : Player<Role> {

  /** Describes pieces of the current player. */
  Allied,

  /** Describes the pieces of the opposite player. */
  Adversary;

  override fun denormalize(role: Role) = role
  override fun normalize(position: Position) = position
  override fun denormalize(position: Position) = position
  override fun normalize(delta: Delta) = delta
  override fun denormalize(delta: Delta) = delta
  override fun other() = if (this == Allied) Adversary else Allied
}
