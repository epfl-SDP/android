package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * A state which indicates the content of an [GameScreen] composable. It will keep track of the
 * values of moves history.
 *
 * This state contains a [Identifier] that is used to idenfity a [Piece]
 */
@Stable
interface GameScreenState<Identifier> : ChessBoardState<Identifier> {
  val moves: List<Move>
}

/** An interface representing a played chess move. */
interface Move {
  val number: Int
  val name: String
}
