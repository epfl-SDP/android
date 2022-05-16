package ch.epfl.sdp.mobile.state.game.delegating

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ar.ArGameScreenState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val TAG = "DelegatingArState"

class DelegatingArState(
    private val match: Match,
    override val chessScene: ChessScene<Piece>,
    private val scope: CoroutineScope
) : ArGameScreenState<Piece>, GameDelegate {

  var lastSaveGame: Game? = null

  override var game: Game by mutableStateOf(Game.create())

  override val pieces
    get() = game.board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  init {
    scope.launch { match.game.collect { game = it } }
  }

  override fun onLoad(boardScale: Float) {
    scope.launch { chessScene.loadStartingBoard(pieces) }.invokeOnCompletion { update() }
    chessScene.scale(boardScale)
  }

  override fun update() {
    Log.d(TAG, "update $lastSaveGame")
    var prev = game.previous
    while (prev != null && prev != lastSaveGame) {
      Log.d(TAG, "update ${prev.second}")
      chessScene.update(prev.second)
      prev = prev.first.previous
    }
    lastSaveGame = game

    chessScene.removeOldPiece(pieces)
  }
}
