package ch.epfl.sdp.mobile.ui.ar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import com.google.ar.core.Anchor
import com.gorisse.thomas.lifecycle.lifecycleScope
import io.github.sceneview.ar.ArSceneView

private const val BoardScale = 0.2f

/**
 * This composable displays a chess game in AR. When it is created, it will load the different
 * models needed to display the scene . To show the board, the user need to wait that ARCore analyse
 * the environment and when it's ready, the user need to tap on the screen and the board will be
 * placed at this position
 *
 * This composable will keep the screen on, it will never sleep
 *
 * @param game The game that will be displayed in AR
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun ArScreen(game: Game, modifier: Modifier = Modifier) {

  var chessScene by remember { mutableStateOf<ChessScene?>(null) }

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

        // Create the object [ChessScene] that will load all the AR elements
        chessScene = ChessScene(context, view.lifecycleScope, game.board)

        // Scale the whole scene to the desired size
        chessScene?.scale(BoardScale)

        /**
         * If not already in the scene, the board will be added. Update the board anchor with the
         * given one and change the displayed position
         *
         * @param anchor The (new) board's anchor position
         */
        fun anchorOrMoveBoard(anchor: Anchor) {

          chessScene?.let {
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
