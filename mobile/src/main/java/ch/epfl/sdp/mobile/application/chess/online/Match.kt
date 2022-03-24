package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.chess.Game

class Match(val game: Game, val whiteId: String?, val blackId: String?) {

  // TODO: How to get rid of this default id
  val gameId: String? = null

  companion object {
    fun create(whiteId: String?, blackId: String?): Match {
      return Match(Game.create(), whiteId, blackId)
    }

    fun create(): Match {
      return create(null, null)
    }
  }
}
