package ch.epfl.sdp.mobile.ui.game.ar

import android.content.Context
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
import io.github.sceneview.ar.arcore.LightEstimationMode
import io.github.sceneview.ar.node.ArModelNode
import kotlinx.coroutines.CoroutineScope

private const val BoardScale = 0.2f

/**
 * Composable used to display a AR chess board.
 *
 * @param Piece the type of the pieces which are present in a board.
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
        val arSceneView =
            ArSceneView(context).apply { lightEstimationMode = LightEstimationMode.SPECTACULAR }

        chessScene =
            createChessScene(
                context = context,
                arSceneView = arSceneView,
                startingBoard = state.pieces,
                lifecycleScope = view.lifecycleScope)

        arSceneView
      },
      modifier = modifier.semantics { this.contentDescription = strings.arContentDescription },
      update = { chessScene?.update(state.pieces) })
}

/**
 * If not already in the scene, the board will be added. Update the board anchor with the given one
 * and change the displayed position
 *
 * @param arSceneView The view where the scene will be displayed
 * @param boardNode The node that will be added into the view
 * @param anchor The (new) board's anchor position
 */
private fun anchorOrMoveBoard(
    // arSceneView: ArSceneView,
    boardNode: ArModelNode,
    anchor: Anchor
) {

  // FIXME : Workaround see line 117

  // Add only one instance of the node
  /*if (!arSceneView.children.contains(boardNode)) {
    arSceneView.addChild(boardNode)
  }*/

  if (!boardNode.isVisible) boardNode.isVisible = true

  boardNode.anchor = anchor
}

/**
 * Create an instance of [ChessScene] and setup the onTouch callback.
 *
 * @param Piece the type of the pieces which are present in a board.
 * @param context The context of the view.
 * @param arSceneView the linked [ArSceneView] where the piece will be display.
 * @param startingBoard the board configuration of the beginning.
 * @param lifecycleScope the lifecycle of the view.
 *
 * @return An instance of [ChessScene].
 */
private fun <Piece : ChessBoardState.Piece> createChessScene(
    context: Context,
    arSceneView: ArSceneView,
    startingBoard: Map<ChessBoardState.Position, Piece>,
    lifecycleScope: CoroutineScope
): ChessScene<Piece> {
  return ChessScene(context, lifecycleScope, startingBoard).apply {

    // Scale the board
    this.scale(BoardScale)

    // Place the chess board on the tapped position.
    arSceneView.onTouchAr =
        { hitResult, _ ->
          anchorOrMoveBoard(/*arSceneView,*/ this.boardNode, hitResult.createAnchor())
        }

    /**
     * FIXME : Workaround : A strange bug make the animation fail when we add the child via a
     * function. To solve this quickly, we add the board when is loaded and set it to invisible. We
     * reset the visibility when the user tap on the screen
     */
    arSceneView.addChild(this.boardNode)
    this.boardNode.isVisible = false
  }
}
