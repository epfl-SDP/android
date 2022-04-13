package ch.epfl.sdp.mobile.ui.game.ar

import android.view.View
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

@Composable
fun <Piece : ChessBoardState.Piece> ArChessBoard(
    state: ArGameScreenState<Piece>,
    modifier: Modifier = Modifier
) {
  val view = LocalView.current
  val strings = LocalLocalizedStrings.current

  // Keep the screen on only for this composable
  DisposableEffect(view) {
    view.keepScreenOn = true
    onDispose { view.keepScreenOn = false }
  }

  AndroidView(
      factory = { context ->

        // Create the view
        val arSceneView = ArSceneView(context)

        val currentChessScene = state.chessScene ?: return@AndroidView arSceneView

        // Create the object [ChessScene] that will load all the AR elements
        // chessScene = ChessScene(context, view.lifecycleScope, state.pieces)

        // Scale the whole scene to the desired size
        currentChessScene.scale(BoardScale)

        /**
         * If not already in the scene, the board will be added. Update the board anchor with the
         * given one and change the displayed position
         *
         * @param anchor The (new) board's anchor position
         */
        fun anchorOrMoveBoard(anchor: Anchor) {

          state.chessScene?.let {
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
  )
}
