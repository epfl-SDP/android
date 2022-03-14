package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

@Stable
interface GameScreenState {
  val moves: List<Move>
}
