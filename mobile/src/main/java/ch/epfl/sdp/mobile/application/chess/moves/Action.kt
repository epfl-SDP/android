package ch.epfl.sdp.mobile.application.chess.moves

import ch.epfl.sdp.mobile.application.chess.Delta
import ch.epfl.sdp.mobile.application.chess.Position

data class Action(val from: Position, val delta: Delta)
