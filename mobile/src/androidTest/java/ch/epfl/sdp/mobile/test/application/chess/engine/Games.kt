package ch.epfl.sdp.mobile.test.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank

/** A collection of common games, which may be used for debugging purposes. */
object Games {

  /** The fastest mate possible in a game of chess. */
  val FoolsMate: GameScope.() -> Unit = {
    Position(5, 6) += Delta(0, -1)
    Position(4, 1) += Delta(0, 2)
    Position(6, 6) += Delta(0, -2)
    Position(3, 0) += Delta(4, 4)
  }

  /** A short stalemate. */
  val Stalemate: GameScope.() -> Unit = {
    Position(4, 6) += Delta(0, -1)
    Position(0, 1) += Delta(0, 2)
    Position(3, 7) += Delta(4, -4)
    Position(0, 0) += Delta(0, 2)
    Position(7, 3) += Delta(-7, 0)
    Position(7, 1) += Delta(0, 2)
    Position(7, 6) += Delta(0, -2)
    Position(0, 2) += Delta(7, 0)
    Position(0, 3) += Delta(2, -2)
    Position(5, 1) += Delta(0, 1)
    Position(2, 1) += Delta(1, 0)
    Position(4, 0) += Delta(1, 1)
    Position(3, 1) += Delta(-2, 0)
    Position(3, 0) += Delta(0, 5)
    Position(1, 1) += Delta(0, -1)
    Position(3, 5) += Delta(4, -4)
    Position(1, 0) += Delta(1, 0)
    Position(5, 1) += Delta(1, 1)
    Position(2, 0) += Delta(2, 2)
  }

  /** A simple game where the white player promotes a pawn to a queen, and eats a black rook. */
  val Promotion: GameScope.() -> Unit = {
    Position(7, 6) += Delta(0, -2)
    Position(6, 1) += Delta(0, 2)
    Position(7, 4) += Delta(-1, -1)
    Position(0, 1) += Delta(0, 1)
    Position(6, 3) += Delta(0, -1)
    Position(0, 2) += Delta(0, 1)
    Position(6, 2) += Delta(1, -1)
    Position(0, 3) += Delta(0, 1)
    tryPromote(Position(7, 1), Delta(-1, -1), Rank.Queen)
    Position(0, 4) += Delta(0, 4)
    Position(6, 0) += Delta(1, 0)
  }
}
