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

  // TODO : Set the list size at initialization
  var pieceNodes by remember {
    mutableStateOf<List<Pair<ModelNode?, ch.epfl.sdp.mobile.application.chess.engine.Position>>>(
        listOf())
  }

  var arBoard by remember { mutableStateOf<ArBoard?>(null) }

  val view = LocalView.current
  val strings = LocalLocalizedStrings.current

  val nodePlacementMode = PlacementMode.PLANE_HORIZONTAL

  // Keep the screen only for this composable
  DisposableEffect(view) {
    view.keepScreenOn = true
    onDispose { view.keepScreenOn = false }
  }

  // FIX ME : Only to simplify the dev process
  val currentBoardState = Game.create().board

  AndroidView(
      factory = { context ->
        val arSceneView = ArSceneView(context)
        boardNode =
            ArModelNode(placementMode = nodePlacementMode).apply {
              loadModelAsync(
                  context = context,
                  glbFileLocation = ChessModels.Board,
                  autoScale = true,
              ) { renderableInstance ->
                val boardBoundingBox = renderableInstance.filamentAsset?.boundingBox!!

                // get height (on y axe) of the board
                // Double the value to get the total height of the box
                val boardYOffset = 2 * boardBoundingBox.halfExtent[1]
                val boardHalfSize = boardBoundingBox.halfExtent[0]

                arBoard = ArBoard(BoardBorderSize, boardYOffset, boardHalfSize)
                val currentBoard = arBoard ?: return@loadModelAsync

                for (p in currentBoardState) {
                  val path =
                      when (p.second.rank) {
                        Rank.King -> ChessModels.King
                        Rank.Bishop -> ChessModels.Bishop
                        Rank.Pawn -> ChessModels.Pawn
                        Rank.Knight -> ChessModels.Knight
                        Rank.Queen -> ChessModels.Queen
                        Rank.Rook -> ChessModels.Rook
                      }

                  val model =
                      ModelNode(position = currentBoard.toArPosition(p.first)).apply {
                        loadModelAsync(
                            context = context,
                            glbFileLocation = path,
                            coroutineScope = view.lifecycleScope,
                        ) { renderableInstance ->
                          val color =
                              when (p.second.color) {
                                Color.Black -> PawniesColors.Black
                                Color.White -> PawniesColors.White
                              }

                          val mat = renderableInstance.material
                          mat.setFloat3("baseColorFactor", color)
                        }
                        if (p.second.rank == Rank.Knight && p.second.color == Color.Black) {
                          modelRotation = Rotation(0f, 180f, 0f)
                        }
                      }

                  boardNode!!.addChild(model)

                  pieceNodes = pieceNodes.toMutableList().apply { add(Pair(model, p.first)) }
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
          // Add only one instance of the node
          if (!arSceneView.children.contains(boardNode!!)) {
            arSceneView.addChild(boardNode!!)
          }
          boardNode!!.anchor = anchor
        }

        // Place the board on the taped position
        arSceneView.onTouchAr = { hitResult, _ -> anchorOrMoveBoard(hitResult.createAnchor()) }

        arSceneView
      },
      modifier = modifier.semantics { this.contentDescription = strings.arContentDescription },
  )
}
