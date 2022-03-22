package ch.epfl.sdp.mobile.ui.ar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

@Composable
fun ArScreen(modifier: Modifier = Modifier) {
  var pawn1 by remember { mutableStateOf<ArModelNode?>(null) }
  var pawn2 by remember { mutableStateOf<ArModelNode?>(null) }

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
  }

  AndroidView(
      factory = { ArSceneView(it) },
      modifier = modifier,
      update = { view ->
        addNode(pawn1, view)
        addNode(pawn2, view)
      })
}
