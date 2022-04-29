package ch.epfl.sdp.mobile.ui.game.ar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import com.google.ar.core.Anchor
import com.gorisse.thomas.lifecycle.lifecycleScope
import io.github.sceneview.ar.ArSceneView

private const val BoardScale = 0.2f

/**
 * Composable used to display a AR chess board
 *
 * @param state The state of the game, it's used to track the modification on the game
 * @param modifier modifier the [Modifier] for this composable.
 */
@Composable
fun <Piece : ChessBoardState.Piece> ArChessBoardScreen(
    state: ChessBoardState<Piece>,
    modifier: Modifier = Modifier
) {
  val view = LocalView.current
  val strings = LocalLocalizedStrings.current

  var chessScene by remember { mutableStateOf<ChessScene<Piece>?>(null) }

  // Keep the screen on only for this composable
  DisposableEffect(view) {
    view.keepScreenOn = true
    onDispose { view.keepScreenOn = false }
  }

  AndroidView(
      factory = { context ->

        // Create the view
        val arSceneView = ArSceneView(context)

        val boardSnapshot = snapshotFlow { state.pieces }

        chessScene =
            ChessScene(context, view.lifecycleScope, boardSnapshot).apply {
              // Scale the whole scene to the desired size
              scale(BoardScale)
            }

        /**
         * If not already in the scene, the board will be added. Update the board anchor with the
         * given one and change the displayed position
         *
         * @param anchor The (new) board's anchor position
         */
        fun anchorOrMoveBoard(anchor: Anchor) {

          val currentChessScene = chessScene ?: return

          currentChessScene.let {
            // Add only one instance of the node
            if (!arSceneView.children.contains(it.boardNode)) {
              arSceneView.addChild(it.boardNode)
            }
            it.boardNode.anchor = anchor
          }
        }

        // Place the chess board on the taped position
        arSceneView.onTouchAr = { hitResult, _ -> anchorOrMoveBoard(hitResult.createAnchor()) }

        arSceneView
      },
      modifier = modifier.semantics { this.contentDescription = strings.arContentDescription },
      update = { chessScene?.savePosition(state.pieces) })
}
