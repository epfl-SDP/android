package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

@Stable
interface GameScreenState<Identifier> : ChessBoardState<Identifier> {
  val moves: List<Move>
}
