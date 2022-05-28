package ch.epfl.sdp.mobile.ui.game.ar

import android.content.Context
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import com.google.android.filament.Box
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.RenderableInstance
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.material.setMetallicFactor
import io.github.sceneview.material.setRoughnessFactor
import io.github.sceneview.math.Position as ArPosition
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.GLBLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color as ArColor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach

/**
 * This class represent the AR chess scene which contains :
 * - The board as the root node
 * - A number of chess pieces depending on the game state
 *
 * The assets used in this scene are from : https://free3d.com/3d-model/chessboard-716865.html
 *
 * @param context The context used to load the 3d models
 * @param scope A scope that is used to launch the model loading
 */
class ChessScene<Piece : ChessBoardState.Piece>(
    private val context: Context,
    private val scope: CoroutineScope,
    startingBoard: Map<Position, Piece>,
) {

  /** The [ArModelNode] which acts as the root of the [ArModelNode] hierarchy. */
  val boardNode = ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL)

  /** A mutable map that tracks the pieces currently displayed in the scene. */
  private val currentPieces: MutableMap<Piece, ModelNode> = mutableMapOf()

  /** A conflated [Channel] which associates the position of the pieces to their values. */
  private val currentPositionChannel =
      Channel<Map<Position, Piece>>(capacity = CONFLATED).apply { trySend(startingBoard) }

  init {

    scope.launch {
      val boardRenderableInstance = prepareBoardRenderableInstance(boardNode) ?: return@launch

      // Board Bounding box
      val boundingBox = boardRenderableInstance.filamentAsset?.boundingBox ?: return@launch

      val pieceRenderable = loadPieceRenderable()

      currentPositionChannel
          .consumeAsFlow()
          .onEach { positionsToPieces ->
            updateBoard(positionsToPieces, pieceRenderable, boundingBox)
          }
          .collect()
    }
  }

  /**
   * Prepares the [ArModelNode] which contains the AR board to be displayed, by loading the
   * appropriate model.
   *
   * @param node the [ArModelNode] to be prepared.
   * @return the [RenderableInstance] which can be used to manipulate the loaded model.
   */
  private suspend fun prepareBoardRenderableInstance(node: ArModelNode): RenderableInstance? {
    return node.loadModel(
        context = context,
        glbFileLocation = ChessModels.Board,
        autoScale = true,
    )
  }

  /**
   * Loads all the [Renderable] for any [Rank] and makes them available as a higher-order function.
   * All the models will be loaded in parallel.
   *
   * @return a higher-order function which maps ranks to the right [Renderable].
   */
  private suspend fun loadPieceRenderable(): (Rank) -> Renderable = coroutineScope {
    val loaded = mutableMapOf<Rank, Renderable>()

    for (rank in Rank.values()) {
      launch { loaded[rank] = loadPieceRenderable(rank) }
    }
    { rank -> requireNotNull(loaded[rank]) }
  }

  /**
   * Loads the [Renderable] for the given [Rank] and [Color]. This [Renderable] may then be used
   * across multiple nodes to be rendered on the chess board.
   *
   * @param rank the [Rank] of the piece to fetch.
   * @return the [Renderable] to be displayed.
   */
  private suspend fun loadPieceRenderable(rank: Rank): Renderable =
      requireNotNull(GLBLoader.loadModel(context, rank.arModelPath))

  /**
   * Creates the renderable of the given [Piece] and adds it to [boardNode] as a child.
   *
   * @param position the position of the piece on the board.
   * @param boundingBox the bounding box of [boardNode].
   * @param pieceRenderable the function that loads a renderable given a [Rank].
   * @param piece the piece that needs to be placed on the board.
   */
  private fun addPiece(
      position: Position,
      boundingBox: Box,
      pieceRenderable: (Rank) -> Renderable,
      piece: Piece
  ) {
    val arPosition = toArPosition(position, boundingBox)
    with(ModelNode(position = arPosition)) {
      val renderable = setModel(pieceRenderable(piece.rank)) ?: return@with

      val mat = renderable.material.filamentMaterialInstance

      // Rotate the black pieces to face the right direction.
      if (piece.color == Black) {
        modelRotation = Rotation(0f, 180f, 0f)
      }

      // Set material parameters
      mat.setBaseColor(piece.color.colorVector)
      mat.setMetallicFactor(0f)
      mat.setRoughnessFactor(0.1f)

      boardNode.addChild(this)
      currentPieces[piece] = this
    }
  }

  /** Scales the whole scene with the given [value]. */
  internal fun scale(value: Float) {
    boardNode.scale(value)
  }

  /**
   * Updates the board with the given pieces.
   *
   * @param pieces the new pieces on the board.
   */
  private fun updateBoard(
      pieces: Map<Position, Piece>,
      pieceRenderable: (Rank) -> Renderable,
      boundingBox: Box
  ) {

    val piecesToPositions = pieces.entries.associate { (k, v) -> v to k }
    val inserted = pieces.values - currentPieces.keys
    val removed = currentPieces.keys - pieces.values
    val updated = currentPieces.keys - removed

    // Remove all the removed pieces.
    for (piece in removed) {
      val node = currentPieces[piece] ?: continue
      boardNode.removeChild(node)
      node.destroy()
      currentPieces -= piece
    }

    // Move all the updated pieces.
    for (piece in updated) {
      val position = piecesToPositions[piece] ?: continue
      val node = currentPieces[piece] ?: continue
      val arPosition = toArPosition(position, boundingBox)
      move(node, arPosition)
    }

    // Insert missing pieces.
    for (piece in inserted) {
      val position = piecesToPositions[piece] ?: continue
      addPiece(position, boundingBox, pieceRenderable, piece)
    }
  }

  /**
   * A custom method that performs smooth displacement of objects following a list of points.
   *
   * @param node The model to move
   * @param position the final position
   * @param height the max height of the animation
   * @param speed of the smooth animation
   */
  private fun move(node: ModelNode, position: ArPosition, height: Float = 2f, speed: Float = 10f) {

    // If the position is the same, don't move
    if (node.position == position) return

    // List of points
    val pathIt =
        listOf(
                ArPosition(node.position.x, height, node.position.z),
                ArPosition(position.x, height, position.z),
                position)
            .iterator()

    var targetPosition = pathIt.next()

    /**
     * Update the target position and start to move the next position
     *
     * @param newPosition the new target position
     */
    fun changeTarget(newPosition: ArPosition) {
      targetPosition = newPosition
      node.smooth(targetPosition)
    }

    // start to move to the 1st position
    node.smooth(targetPosition, speed = speed)

    // Add a callback
    node.onFrame.add { _, _ ->
      if (node.position == targetPosition && pathIt.hasNext()) {
        changeTarget(pathIt.next())
      }
    }
  }

  /**
   * Used by [ArChessBoardScreen] to update the pieces' positions on the board.
   *
   * @param pieces a map of the pieces and their new positions.
   */
  fun update(pieces: Map<Position, Piece>) {
    currentPositionChannel.trySend(pieces)
  }

  companion object {
    // This value cannot be computed, it's chosen by guess
    const val BoardBorderSize = 2.2f
  }
}

/**
 * For the given [Position] and transform it into AR board [ArPosition].
 *
 * @param position the [Position] which should be mapped to an [ArPosition].
 * @param boundingBox the [Box] of the board in which the cells are to be placed.
 */
private fun toArPosition(
    position: Position,
    boundingBox: Box,
): ArPosition {

  val boardHeight = 2 * boundingBox.halfExtent[1]
  val boardHalfSize = boundingBox.halfExtent[0]

  val cellSize = (boardHalfSize - ChessScene.BoardBorderSize) / 4
  val cellCenter = cellSize / 2

  fun transform(value: Int): Float {
    return -boardHalfSize + ChessScene.BoardBorderSize + value * cellSize + cellCenter
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

/** Convert the [Color] into a color that can be used by the AR renderer */
private val Color.colorVector: ArColor
  get() =
      when (this) {
        Black -> PawniesArColors.Black
        White -> PawniesArColors.White
      }
