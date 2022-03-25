package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.chess.Game

/**
 * Represents a [Game] between two online [Profiles] (through their UIDs)
 *
 * @param game The latest [Game] state that was played
 * @param gameId The online match document's UID
 * @param whiteId The UID of the player playing white
 * @param blackId The UID of the player playing black
 */
data class Match(val game: Game, val gameId: String?, val whiteId: String?, val blackId: String?) {

  companion object {
    fun create(whiteId: String?, blackId: String?): Match {
      return Match(Game.create(), null, whiteId, blackId)
    }

    fun create(): Match {
      return create(null, null)
    }
  }
}
