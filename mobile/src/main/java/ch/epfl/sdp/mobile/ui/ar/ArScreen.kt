package ch.epfl.sdp.mobile.ui.ar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*
import com.google.ar.core.Anchor
import com.google.ar.sceneform.math.Vector3
import com.gorisse.thomas.lifecycle.lifecycleScope
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode

private const val BoardScale = 0.2f
// This value cannot be computed, it's selected by test and try
private const val BoardBorderSize = 2.2f

@Composable
fun ArScreen(modifier: Modifier = Modifier) {

  var boardNode by remember { mutableStateOf<ArModelNode?>(null) }

  var arBoard by remember { mutableStateOf<ArBoard?>(null) }

  val view = LocalView.current
  val strings = LocalLocalizedStrings.current

  // Keep the screen only for this composable
  DisposableEffect(view) {
    view.keepScreenOn = true
    onDispose { view.keepScreenOn = false }
  }

  // FIX ME : Only to simplify the dev process
  val currentBoardState = Game.create().board

  AndroidView(
      factory = { context ->

        /** Return the corresponding model path given the [rank] */
        fun getModelPath(rank: Rank): String {
          return when (rank) {
            Rank.King -> ChessModels.King
            Rank.Bishop -> ChessModels.Bishop
            Rank.Pawn -> ChessModels.Pawn
            Rank.Knight -> ChessModels.Knight
            Rank.Queen -> ChessModels.Queen
            Rank.Rook -> ChessModels.Rook
          }
        }

        /** Return the model color as a [Vector3] given the piece [color] */
        fun getModelColor(color: Color): Vector3 {
          return when (color) {
            Color.Black -> PawniesColors.Black
            Color.White -> PawniesColors.White
          }
        }

        // Create the view
        val arSceneView = ArSceneView(context)

        // Create the board's node
        boardNode =
            ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL).apply {
              // Load the board
              loadModelAsync(
                  context = context,
                  glbFileLocation = ChessModels.Board,
                  autoScale = true,
              ) { renderableInstance ->

                // Once loaded compute the board size
                val boardBoundingBox = renderableInstance.filamentAsset?.boundingBox!!

                // Get height (on y axe) of the board
                // Double the value to get the total height of the box
                val boardYOffset = 2 * boardBoundingBox.halfExtent[1]
                val boardHalfSize = boardBoundingBox.halfExtent[0]

                // Create only if it the first time
                if (arBoard == null) {
                  arBoard = ArBoard(BoardBorderSize, boardYOffset, boardHalfSize)
                }

                val currentBoard = arBoard ?: return@loadModelAsync

                // Initialize all pieces
                for ((position, piece) in currentBoardState) {

                  val path = getModelPath(piece.rank)

                  val model =
                      ModelNode(position = currentBoard.toArPosition(position)).apply {
                        // Load the piece
                        loadModelAsync(
                            context = context,
                            glbFileLocation = path,
                            coroutineScope = view.lifecycleScope,
                        ) { renderableInstance ->
                          // Once loaded change the piece appearance

                          val color = getModelColor(piece.color)

                          renderableInstance.material.setFloat3("baseColorFactor", color)
                        }

                        // Rotate the black knight to be faced inside the board
                        if (piece.rank == Rank.Knight && piece.color == Color.Black) {
                          modelRotation = Rotation(0f, 180f, 0f)
                        }
                      }

                  // Add the model on the board
                  boardNode?.let { addChild(model) }
                }
              }
              scale(BoardScale)
            }

        /**
         * If not already in the scene, the board will be added. Update the board anchor with the
         * given one and change the displayed position
         *
         * @param anchor The (new) board's anchor position
         */
        fun anchorOrMoveBoard(anchor: Anchor) {
          val currentBoard = boardNode ?: return

          // Add only one instance of the node
          if (!arSceneView.children.contains(currentBoard)) {
            arSceneView.addChild(currentBoard)
          }
          currentBoard.anchor = anchor
        }

        // Place the chess board on the taped position
        arSceneView.onTouchAr = { hitResult, _ -> anchorOrMoveBoard(hitResult.createAnchor()) }

        arSceneView
      },
      modifier = modifier.semantics { this.contentDescription = strings.arContentDescription },
  )
}
