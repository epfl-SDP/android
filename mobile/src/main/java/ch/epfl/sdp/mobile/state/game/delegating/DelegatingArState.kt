package ch.epfl.sdp.mobile.state.game.delegating

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

/**
 * An implementation of [ArGameScreenState]
 *
 * @param match The match that we spectate
 * @param chessScene the scene that will be displayed
 * @param scope the [CoroutineScope] on which pieces loading are performed.
 */
class DelegatingArState(private val match: Match, private val scope: CoroutineScope) :
    ArGameScreenState<Piece>, GameDelegate {

  override var game: Game by mutableStateOf(Game.create())

  override val pieces
    get() = game.board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  override val chessScene: ChessScene<Piece> = ChessScene(scope, pieces)

  init {
    scope.launch { match.game.collect { game = it } }
  }
}
