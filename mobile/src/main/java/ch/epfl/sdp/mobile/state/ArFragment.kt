package ch.epfl.sdp.mobile.state

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ch.epfl.sdp.mobile.R
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.model.GLBLoader.loadModelAsync

class ArFragment : Fragment(R.layout.ar_fragment) {

  lateinit var sceneView: ArSceneView

  lateinit var modelNode: ArModelNode

  lateinit var modelNode2: ArModelNode

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    sceneView = view.findViewById(R.id.sceneView)

    // Create the node containing the 3d model
    modelNode =
        ArModelNode(placementMode = PlacementMode.BEST_AVAILABLE, autoAnchor = true).apply {
          loadModelAsync(
              context = requireContext(),
              glbFileLocation = "models/pawn.glb",
              coroutineScope = lifecycleScope,
              autoScale = true,
              onLoaded = {
                val boundingBox = it.filamentAsset?.boundingBox
                val halfExtent = boundingBox?.halfExtent?.maxOrNull()!!
                scale = Scale(0.5f / halfExtent)
              })
        }
    modelNode2 =
        ArModelNode(
                placementMode = PlacementMode.BEST_AVAILABLE,
                autoAnchor = true,
                placementPosition = Position(x = 2f))
            .apply {
              loadModelAsync(
                  context = requireContext(),
                  glbFileLocation = "models/pawn.glb",
                  coroutineScope = lifecycleScope,
                  autoScale = true,
                  onLoaded = {
                    val boundingBox = it.filamentAsset?.boundingBox
                    val halfExtent = boundingBox?.halfExtent?.maxOrNull()!!
                    scale = Scale(0.5f / halfExtent)
                  })
            }

    // Attach the models to the scene
    sceneView.addChild(modelNode)
    sceneView.addChild(modelNode2)
  }
}
