package ch.epfl.sdp.mobile.ui.game.ar

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import ch.epfl.sdp.mobile.application.chess.engine.PieceIdentifier
import ch.epfl.sdp.mobile.state.SnapshotChessBoardState
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Position as ArPosition
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color as ArColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val TAG = "ChessScene"

/**
 * This class represent the AR chess scene which contains :
 * - The board as the root node
 * - A number of chess pieces depending on the game state
 *
 * @param context The context used to load the 3d models
 * @param lifecycleScope A scope that is used to launch the model loading
 * @param boardSnapshot The board that contains the displayed game state
 */
class ChessScene(
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    boardSnapshot: Flow<Map<Position, SnapshotChessBoardState.SnapshotPiece>>,
) {
  val boardNode: ArModelNode = ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL)

  private var boardHeight: Float = 0f
  private var boardHalfSize: Float = 0f

  private var loadBoardJob: Job =
      lifecycleScope.launch {
        boardNode.loadModel(
            context = context,
            glbFileLocation = ChessModels.Board,
            autoScale = true,
        )
      }
  private lateinit var loadPiecesJob: Job

  private val currentPieces: MutableMap<PieceIdentifier, ModelNode> = mutableMapOf()

  init {

    loadBoardJob.invokeOnCompletion {
      val renderableInstance = boardNode.modelInstance ?: return@invokeOnCompletion
      // Once loaded compute the board size
      val filamentAsset = renderableInstance.filamentAsset ?: return@invokeOnCompletion
      val boardBoundingBox = filamentAsset.boundingBox

      // Get height (on y axe) of the board
      // Double the value to get the total height of the box
      boardHeight = 2 * boardBoundingBox.halfExtent[1]
      boardHalfSize = boardBoundingBox.halfExtent[0]
      loadPiecesJob = lifecycleScope.launch { loadPieces(boardSnapshot.first()) }
    }

    lifecycleScope.launch {
      boardSnapshot.collect { map ->
        for ((position, piece) in map) {
          Log.d(TAG, "Board $position $piece")
          move(piece.id, position)
        }
      }
    }
  }

  private fun move(id: PieceIdentifier, position: Position) {
    loadBoardJob.invokeOnCompletion {
      loadPiecesJob.invokeOnCompletion {
        val model = currentPieces[id]
        model?.smooth(toArPosition(position))
      }
    }
  }

  /**
   * Load a chess [piece] and place it to a given [position] relative to the parent (aka the
   * chessboard)
   */
  private fun loadPieceModel(
      piece: SnapshotChessBoardState.SnapshotPiece,
      position: Position,
  ): ModelNode {
    val path = piece.rank.arModelPath

    val model =
        ModelNode(position = toArPosition(position)).apply {
          // Load the piece
          loadModelAsync(
              context = context, glbFileLocation = path, coroutineScope = lifecycleScope) {
              renderableInstance ->
            // Once loaded change the piece appearance

            val color = piece.color.colorVector

            renderableInstance.material.filamentMaterialInstance.setBaseColor(color)
          }

          // Rotate the black knight to be faced inside the board
          if (piece.rank == Knight && piece.color == Black) {
            modelRotation = Rotation(0f, 180f, 0f)
          }
        }

    return model
  }

  private fun loadPieces(pieces: Map<Position, SnapshotChessBoardState.SnapshotPiece>) {
    for ((position, piece) in pieces) {
      val model = loadPieceModel(piece, position)
      // Add the new model node to the board
      boardNode.addChild(model)
      currentPieces[piece.id] = model
    }
  }

  /** Scale the whole scene with the given [value] */
  internal fun scale(value: Float) {
    boardNode.scale(value)
  }

  companion object {
    // This value cannot be computed, it's chosen by guess
    private const val BoardBorderSize = 2.2f
  }

  /** For the given [Position] and transform it into AR board [ArPosition] */
  private fun toArPosition(position: Position): ArPosition {

    val cellSize = (boardHalfSize - BoardBorderSize) / 4
    val cellCenter = cellSize / 2

    fun transform(value: Int): Float {
      return -boardHalfSize + BoardBorderSize + value * cellSize + cellCenter
    }
    return ArPosition(x = transform(position.x), y = boardHeight, z = transform(position.y))
  }

  /** Transform a [Rank] into the corresponding model's path */
  private val Rank.arModelPath: String
    get() =
        when (this) {
          King -> ChessModels.King
          Bishop -> ChessModels.Bishop
          Pawn -> ChessModels.Pawn
          Knight -> ChessModels.Knight
          Queen -> ChessModels.Queen
          Rook -> ChessModels.Rook
        }

  // TODO Use Color instead of Vector
  /** Convert the [Color] into a color that can be used by the AR renderer */
  private val Color.colorVector: ArColor
    get() =
        when (this) {
          Black -> PawniesArColors.Black
          White -> PawniesArColors.White
        }
}
