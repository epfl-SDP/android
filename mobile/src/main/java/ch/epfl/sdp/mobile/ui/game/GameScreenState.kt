package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

@Stable
interface GameScreenState<Identifier> : ChessBoardState<Identifier> {
  val moves: List<Move>
}

/** An interface representing a played chess move. */
interface Move {
  val number: Int
  val name: String
}
