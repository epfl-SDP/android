package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * A state which indicates the content of an [GameScreen] composable. It will keep track of the
 * values of moves history.
 *
 * This state uses a specific [Piece] type.
 */
@Stable
interface GameScreenState<Piece : ChessBoardState.Piece> : ChessBoardState<Piece> {
  val moves: List<Move>
}

/** An interface representing a played chess move. */
interface Move {
  val number: Int
  val name: String
}
