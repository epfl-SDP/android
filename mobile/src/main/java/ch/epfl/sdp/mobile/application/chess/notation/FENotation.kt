package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotationCombinators.action
import ch.epfl.sdp.mobile.ui.game.ChessBoardCells

/**
 * An object which contains some utilities to transform games in algebraic notation, and
 * vice-versa.
 */
object FENotation {

  private fun FENChessboard(text: String): String {
    return ""
  }
  private fun FENPlayerColor(text: String): String {
    return ""
  }


  fun parseFEN(text: String): Game {
    var game = buildGame(Color.White) {

    }

    return game
  }

  /**
   * Transforms this [Game] to extended algebraic notation.
   *
   * @receiver the [Game] that is transformed.
   */
  fun Game.toFEN(): String = ""
}
