package ch.epfl.sdp.mobile.ui.ar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ar.core.Anchor
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

// ONLY FOR DEBUGGING
// FIXME : Need to remove it when the project finish
private const val DisplayAxes = true

@Composable
fun ArScreen(modifier: Modifier = Modifier) {
  var pawn1 by remember { mutableStateOf<ArModelNode?>(null) }
  var pawn2 by remember { mutableStateOf<ArModelNode?>(null) }

  var board by remember { mutableStateOf<ArModelNode?>(null) }
  var boardYOffset by remember { mutableStateOf(0f) }
  // DEBUG
  var white by remember { mutableStateOf<ArModelNode?>(null) }
  var red by remember { mutableStateOf<ArModelNode?>(null) }
  var blue by remember { mutableStateOf<ArModelNode?>(null) }
  var green by remember { mutableStateOf<ArModelNode?>(null) }

  val context = LocalContext.current
  val nodePlacementMode = PlacementMode.PLANE_HORIZONTAL

  /**
   * Load the given model, scale it and store it as a [ArModelNode]
   *
   * @param glbPath The string indicating the model location
   * @param placementMode Indicate how to position the model in the world. See documentation
   * [PlacementMode] to see the possible value
   * @param position The model position in to world relative to the center
   */
  suspend fun loadModel(
      glbPath: String,
      placementMode: PlacementMode = ArModelNode.DEFAULT_PLACEMENT_MODE,
      position: Position = ArModelNode.DEFAULT_PLACEMENT_POSITION
  ): ArModelNode {
    val node =
        ArModelNode(placementMode = placementMode, autoAnchor = true, placementPosition = position)
    val instance =
        node.loadModel(
            context = context,
            glbFileLocation = glbPath,
            autoScale = true,
        )
    val boundingBox = instance?.filamentAsset?.boundingBox
    val halfExtent = boundingBox?.halfExtent?.maxOrNull()!!
    // TODO : Determine the scale
    node.scale(0.5f / halfExtent)
    return node
  }

  /**
   * Add the given [ArModelNode] to the scene
   *
   * @param modelNode the model that we want to add
   * @param view Where the model will be added
   */
  fun addNode(modelNode: ArModelNode?, view: ArSceneView) {
    val node = modelNode ?: return
    if (node !in view.children) {
      view.addChild(node)
    }
  }

  LaunchedEffect(Unit) {
    pawn1 = loadModel("models/pawn.glb", nodePlacementMode)
    pawn2 = loadModel("models/pawn.glb", nodePlacementMode, Position(x = 1f))
    board = loadModel("models/board.glb", nodePlacementMode)

    val boardBoundingBox = board!!.modelInstance?.filamentAsset?.boundingBox

    // get Y
    if (boardBoundingBox != null) {
      // Double the value to get the total height of the box
      boardYOffset = 2 * boardBoundingBox.halfExtent[1]
    }
    // DEBUG
    if (DisplayAxes) {
      white = loadModel("models/white.glb", nodePlacementMode)
      red = loadModel("models/red.glb", nodePlacementMode)
      blue = loadModel("models/blue.glb", nodePlacementMode)
      green = loadModel("models/green.glb", nodePlacementMode)
    }
  }

  AndroidView(
      factory = { ArSceneView(it) },
      modifier = modifier,
      update = { view ->
        if (DisplayAxes) {
          white?.let {
            it.scale(2f)
            board?.addChild(it)
            it.placementPosition = Position(y = boardYOffset)
          }
          red?.let {
            it.scale(2f)
            board?.addChild(it)
            it.placementPosition = Position(x = 3f, y = boardYOffset)
          }
          blue?.let {
            it.scale(2f)
            board?.addChild(it)
            it.placementPosition = Position(z = 3f, y = boardYOffset)
          }
          green?.let {
            it.scale(2f)
            board?.addChild(it)
            it.placementPosition = Position(y = 3f + boardYOffset)
          }
        }
        pawn1?.let {
          board?.addChild(it)
          it.placementPosition = Position(y = boardYOffset)
        }

        board?.scale(0.2f)

        fun anchorOrMove(anchor: Anchor) {
          // Add only one instance of the node
          if (!view.children.contains(board!!)) {
            view.addChild(board!!)
          }
          board?.anchor = anchor
        }

        // Place the board on the taped position
        view.onTouchAr = { hitResult, _ -> anchorOrMove(hitResult.createAnchor()) }
      })
}
