package ch.epfl.sdp.mobile.application.chess.rules

import ch.epfl.sdp.mobile.application.chess.Delta
import ch.epfl.sdp.mobile.application.chess.Position

/**
 * An [Action] represents an action that one player undertakes in the game. Actions have a start
 * position (corresponding to a piece that the player wants to play), and a delta, corresponding to
 * where they position the piece on the board (relative to the start position).
 *
 * @param from the position from which the piece is dragged.
 * @param delta the amount by which the piece is moved.
 */
data class Action(val from: Position, val delta: Delta)
