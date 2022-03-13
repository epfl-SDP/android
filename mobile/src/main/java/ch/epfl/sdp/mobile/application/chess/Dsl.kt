package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.internal.PersistentGame

/** Creates a new [Game], with the standard starting positions for both players. */
fun emptyGame(): Game = PersistentGame(nextPlayer = Color.White)
