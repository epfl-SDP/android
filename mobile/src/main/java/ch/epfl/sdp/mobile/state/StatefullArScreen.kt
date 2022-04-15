package ch.epfl.sdp.mobile.state

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.LifecycleCoroutineScope
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.BasicSnapshotBoardState.SnapshotPiece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ar.ArChessBoard
import ch.epfl.sdp.mobile.ui.game.ar.ArGameScreenState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import com.gorisse.thomas.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

/**
 * A composable that make [ArChessBoard] stateful
 *
 * @param id the identifier for the match.
 * @param modifier the [Modifier] for the composable.
 */
@Composable
fun StatefulArScreen(
    id: String,
    modifier: Modifier = Modifier,
) {

  val context = LocalContext.current
  val lifecycleScope = LocalView.current.lifecycleScope

  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val match = remember(facade, id) { facade.match(id) }

  val gameScreenState =
      remember(context, lifecycleScope, match, scope) {
        SnapshotArChessBoardState(context, lifecycleScope, match, scope)
      }

  ArChessBoard(gameScreenState, modifier)
}

/**
 * An implementation of [ChessBoardState] that starts with default chess position that can be
 * display in AR.
 *
 * @param context The context used to load the 3d models
 * @param lifecycleScope A scope that is used to launch the model loading
 * @param match the match to display.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 */
class SnapshotArChessBoardState(
    val context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    match: Match,
    scope: CoroutineScope,
) : ArGameScreenState<SnapshotPiece>, BasicSnapshotBoardState(match, scope) {

  override val chessScene: ChessScene<SnapshotPiece> = ChessScene(context, lifecycleScope, pieces)

  override fun scale(value: Float) {
    chessScene.scale(value)
  }
}
