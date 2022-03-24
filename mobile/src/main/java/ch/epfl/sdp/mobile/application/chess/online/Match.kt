package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.chess.Game

class Match(val game: Game, val gameId: String?, val whiteId: String?, val blackId: String?) {

  companion object {
    fun create(whiteId: String?, blackId: String?): Match {
      return Match(Game.create(), null, whiteId, blackId)
    }

    fun create(): Match {
      return create(null, null)
    }
  }
}
