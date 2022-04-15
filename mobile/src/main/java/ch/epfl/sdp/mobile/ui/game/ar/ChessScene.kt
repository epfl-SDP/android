package ch.epfl.sdp.mobile.ui.game.ar

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode

/**
 * This class represent the AR chess scene which contains :
 * - The board as the root node
 * - A number of chess pieces depending on the game state
 *
 * @param context The context used to load the 3d models
 * @param lifecycleScope A scope that is used to launch the model loading
 * @param board The board that contains the displayed game state
 */
class ChessScene<Piece : ChessBoardState.Piece>(
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    board: Map<Position, Piece>,
) {

  private lateinit var arBoard: ArBoard

  val boardNode: ArModelNode

  init {
    boardNode =
        ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL).apply {
          // Load the board
          loadModelAsync(
              context = context,
              glbFileLocation = ChessModels.Board,
              coroutineScope = lifecycleScope,
              autoScale = true,
          ) { renderableInstance ->
            // Once loaded compute the board size
            val filamentAsset = renderableInstance.filamentAsset ?: return@loadModelAsync
            val boardBoundingBox = filamentAsset.boundingBox

            // Get height (on y axe) of the board
            // Double the value to get the total height of the box
            val boardYOffset = 2 * boardBoundingBox.halfExtent[1]
            val boardHalfSize = boardBoundingBox.halfExtent[0]

            arBoard = ArBoard(BoardBorderSize, boardYOffset, boardHalfSize)

            // Initialize all pieces
            for ((position, piece) in board) {
              val model = loadPieceModel(piece, position)
              // Add the new model node to the board
              addChild(model)
            }
          }
        }
  }

  /**
   * Load a chess [piece] and place it to a given [position] relative to the parent (aka the
   * chessboard)
   */
  private fun loadPieceModel(
      piece: Piece,
      position: Position,
  ): ModelNode {
    val path = piece.rank.arModelPath

    val model =
        ModelNode(position = arBoard.toArPosition(position)).apply {
          // Load the piece
          loadModelAsync(
              context = context, glbFileLocation = path, coroutineScope = lifecycleScope) {
              renderableInstance ->
            // Once loaded change the piece appearance

            val color = piece.color.colorVector

            renderableInstance.material.setFloat3("baseColorFactor", color)
          }

          // Rotate the black knight to be faced inside the board
          if (piece.rank == Knight && piece.color == Black) {
            modelRotation = Rotation(0f, 180f, 0f)
          }
        }

    return model
  }

  /** Scale the whole scene with the given [value] */
  internal fun scale(value: Float) {
    boardNode.scale(value)
  }

  companion object {
    // This value cannot be computed, it's chosen by guess
    private const val BoardBorderSize = 2.2f
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
  private val Color.colorVector: Vector3
    get() =
        when (this) {
          Black -> PawniesArColors.Black
          White -> PawniesArColors.White
        }
}
