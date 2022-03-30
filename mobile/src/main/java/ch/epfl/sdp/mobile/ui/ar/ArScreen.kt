package ch.epfl.sdp.mobile.ui.ar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import com.google.ar.core.Anchor
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

// ONLY FOR DEBUGGING
// FIXME : Need to remove it when the project finish
private const val DisplayAxes = false

private const val BoardScale = 0.2f
// This value cannot be computed, it's selected by test and try
private const val BoardBorderSize = 2.2f

private const val BoardPath = "models/board.glb"

@Composable
fun ArScreen(modifier: Modifier = Modifier) {

  var pawn1 by remember { mutableStateOf<ArModelNode?>(null) }
  var board by remember { mutableStateOf<ArModelNode?>(null) }

  var arBoard by remember { mutableStateOf<ArBoard?>(null) }

  // FIX ME : Prototype purpose only, need to be replace with the [ChessBoardState]
  val piecePosition = remember { ChessBoardState.Position(1, 1) }

  // DEBUG
  var white by remember { mutableStateOf<ArModelNode?>(null) }
  var red by remember { mutableStateOf<ArModelNode?>(null) }
  var blue by remember { mutableStateOf<ArModelNode?>(null) }
  var green by remember { mutableStateOf<ArModelNode?>(null) }

  val context = LocalContext.current
  val view = LocalView.current
  val strings = LocalLocalizedStrings.current

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
    node.loadModel(
        context = context,
        glbFileLocation = glbPath,
        autoScale = true,
    )
    return node
  }

  // Keep the screen only for this composable
  DisposableEffect(view) {
    view.keepScreenOn = true
    onDispose { view.keepScreenOn = false }
  }

  // Load 3d Model and initialize the [ArBoard]
  LaunchedEffect(Unit) {
    // TODO : Maybe create a enum with all the pieces that contain the path
    pawn1 = loadModel("models/pawn.glb", nodePlacementMode)
    board = loadModel(BoardPath, nodePlacementMode)

    val boardBoundingBox = board!!.modelInstance?.filamentAsset?.boundingBox!!

    // get height (on y axe) of the board
    // Double the value to get the total height of the box
    val boardYOffset = 2 * boardBoundingBox.halfExtent[1]
    val boardHalfSize = boardBoundingBox.halfExtent[0]

    arBoard = ArBoard(BoardBorderSize, boardYOffset, boardHalfSize)

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
      modifier = modifier.semantics { this.contentDescription = strings.arContentDescription },
      update = { view ->
        val currentBoard = board ?: return@AndroidView
        val currentArBoard = arBoard ?: return@AndroidView
        val currentPawn = pawn1 ?: return@AndroidView

        if (DisplayAxes) {

          val currentWhite = white ?: return@AndroidView
          val currentRed = red ?: return@AndroidView
          val currentBlue = blue ?: return@AndroidView
          val currentGreen = green ?: return@AndroidView

          currentWhite.apply {
            scale(2f)
            currentBoard.addChild(this)
            placementPosition = Position(y = currentArBoard.boardHeight)
          }

          currentRed.apply {
            scale(2f)
            currentBoard.addChild(this)
            placementPosition = Position(x = 3f, y = currentArBoard.boardHeight)
          }

          currentBlue.apply {
            scale(2f)
            currentBoard.addChild(this)
            placementPosition = Position(z = 3f, y = currentArBoard.boardHeight)
          }

          currentGreen.apply {
            scale(2f)
            currentBoard.addChild(this)
            placementPosition = Position(y = 3f + currentArBoard.boardHeight)
          }
        }

        /** Add the given [piece] on the board in the correct position */
        fun addPiece(piece: ArModelNode) {
          piece.let {
            currentBoard.addChild(it)
            it.placementPosition = currentArBoard.toArPosition(piecePosition)
          }
        }

        /**
         * TODO : With the [ChessBoardState], iterate over the list of pieces, and do the same that
         * [pawn1]
         */
        addPiece(currentPawn)

        // Scale down the board size
        // As all the pieces are the board children, they scale as well
        currentBoard.scale(BoardScale)

        /**
         * If not already in the scene, the board will be added. Update the board anchor with the
         * given one and change the displayed position
         *
         * @param anchor The (new) board's anchor position
         */
        fun anchorOrMoveBoard(anchor: Anchor) {
          // Add only one instance of the node
          if (!view.children.contains(currentBoard)) {
            view.addChild(currentBoard)
          }
          currentBoard.anchor = anchor
        }

        // Place the board on the taped position
        view.onTouchAr = { hitResult, _ -> anchorOrMoveBoard(hitResult.createAnchor()) }
      })
}
