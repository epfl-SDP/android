package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.implementation.PersistentBoard
import ch.epfl.sdp.mobile.application.chess.implementation.PersistentGame

/** Creates a new [Game], with the standard starting positions for both players. */
fun emptyGame(): Game = PersistentGame(nextPlayer = Color.White, board = PersistentBoard())
