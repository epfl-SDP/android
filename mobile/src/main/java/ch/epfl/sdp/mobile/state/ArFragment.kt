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

class ArFragment : Fragment(R.layout.ar_fragment) {

  lateinit var sceneView: ArSceneView

  lateinit var modelNode: ArModelNode

  lateinit var modelNode2: ArModelNode

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    sceneView = view.findViewById(R.id.sceneView)

    modelNode =
        ArModelNode(placementMode = PlacementMode.BEST_AVAILABLE, autoAnchor = true).apply {
          loadModelAsync(
              context = requireContext(),
              glbFileLocation = "models/pawn.glb",
              coroutineScope = lifecycleScope,
              autoScale = true,
          )
        }
    modelNode2 =
        ArModelNode(placementMode = PlacementMode.BEST_AVAILABLE, autoAnchor = true).apply {
          loadModelAsync(
              context = requireContext(),
              glbFileLocation = "models/pawn.glb",
              coroutineScope = lifecycleScope,
              autoScale = true,
              centerOrigin = Position(y = 1f))
        }


    modelNode.scale(0.5f)
    modelNode2.scale(0.5f)

    sceneView.addChild(modelNode)
    sceneView.addChild(modelNode2)

  }
}
