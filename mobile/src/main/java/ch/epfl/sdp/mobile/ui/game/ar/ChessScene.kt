package ch.epfl.sdp.mobile.ui.game.ar

import android.content.Context
import android.util.Log
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
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
import io.github.sceneview.math.Position as ArPosition
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.GLBLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color as ArColor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

val TAG: String = "ChessScene"
/**
 * This class represent the AR chess scene which contains :
 * - The board as the root node
 * - A number of chess pieces depending on the game state
 *
 * @param context The context used to load the 3d models
 * @param scope A scope that is used to launch the model loading
 * @param startingBoard The board that contains the displayed game state
 */
class ChessScene<Piece : ChessBoardState.Piece>(
    scope: CoroutineScope,
// startingBoard: Map<Position, Piece>,
) {

  /** The [ArModelNode] which acts as the root of the [ArModelNode] hierarchy. */
  val boardNode = ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL)

  /** A conflated [Channel] which associates the position of the pieces to their values. */
  //  private val currentPositionChannel =
  //      Channel<Map<Position, Piece>>(capacity = CONFLATED).apply { trySend(startingBoard) }

  private val currentPieces: MutableMap<Position, Pair<Piece, ModelNode>> = mutableMapOf()

  private var boundingBox: Box? = null

  var context: Context? = null

  init {
    scope.launch {

      // Load Board
      val boardRenderableInstance = prepareBoardRenderableInstance(boardNode) ?: return@launch
      boundingBox = boardRenderableInstance.filamentAsset?.boundingBox ?: return@launch
      val pieceRenderable = loadPieceRenderable()

      //      currentPositionChannel
      //          .consumeAsFlow()
      //          .onEach { positions ->
      //
      //            // FIXME : Removing all nodes will increase the computational resources a lot.
      //            //    We shouldn't destroy a node to re-set it after. Each time that a board
      // change, for
      //            //    each piece, we will create all the loader needed to create asserts.
      // Furthermore,
      //            //    this implementation don't allow use to add animation on models
      //            //    Solution, we save the map of id and rank for each piece.
      //            //    - If the id is missing delete the corresponding node.
      //            //    - If the receive rank didn't match, set the model to the new corresponding
      // rank
      //            //    (Note: Maybe we can check the model path to check if the rank is correct)
      //            // Remove all current children.
      //            boardNode.children.forEach {
      //              val child = boardNode.removeChild(it)
      //              child.destroy()
      //            }
      //
      //            // Add all the pieces at the appropriate position.
      //            for ((position, piece) in positions) {
      //              val arPosition = toArPosition(position, boundingBox)
      //              with(ModelNode(position = arPosition)) {
      //                val renderable = setModel(pieceRenderable(piece.rank)) ?: return@with
      //                // Rotate the black pieces to face the right direction.
      //                if (piece.color == Black) {
      //                  modelRotation = Rotation(0f, 180f, 0f)
      //                }
      //
      // renderable.material.filamentMaterialInstance.setBaseColor(piece.color.colorVector)
      //                boardNode.addChild(this)
      //              }
      //            }
      //          }
      //          .collect()
    }
  }

  /** Update the board incrementally */
  fun update(action: Action) {
    Log.d(TAG, "update $action")
    when (action) {
      is Action.Move -> {
        Log.d(TAG, "current piece : $currentPieces")

        movePiece(action.from.toPosition(), action.from.plus(action.delta)!!.toPosition())
      }
      is Action.Promote -> {
        Log.d(TAG, "Promotion")
      }
    }
  }

  /**
   * Prepares the [ArModelNode] which contains the AR board to be displayed, by loading the
   * appropriate model.
   *
   * @param node the [ArModelNode] to be prepared.
   * @return the [RenderableInstance] which can be used to manipulate the loaded model.
   */
  private suspend fun prepareBoardRenderableInstance(node: ArModelNode): RenderableInstance? =
      node.loadModel(
          context = context!!,
          glbFileLocation = ChessModels.Board,
          autoScale = true,
      )

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
  private suspend fun loadPieceRenderable(
      rank: Rank,
  ): Renderable = requireNotNull(GLBLoader.loadModel(context!!, rank.arModelPath))

  suspend fun loadStartingBoard(startingBoard: Map<Position, Piece>) {
    Log.d(TAG, "Loading pieces $startingBoard")

    // Load Board
    val boardRenderableInstance = prepareBoardRenderableInstance(boardNode) ?: return
    val boundingBox = boardRenderableInstance.filamentAsset?.boundingBox ?: return
    val pieceRenderable = loadPieceRenderable()

    for ((position, piece) in startingBoard) {
      val arPosition = toArPosition(position, boundingBox)
      with(ModelNode(position = arPosition)) {
        val renderable = setModel(pieceRenderable(piece.rank)) ?: return@with
        // Rotate the black pieces to face the right direction.
        if (piece.color == Black) {
          modelRotation = Rotation(0f, 180f, 0f)
        }
        renderable.material.filamentMaterialInstance.setBaseColor(piece.color.colorVector)
        boardNode.addChild(this)
        currentPieces[position] = Pair(piece, this)
      }
    }
  }

  fun movePiece(from: Position, to: Position) {
    val (rank, model) = currentPieces[from] ?: return
    currentPieces.remove(from)

    model.position = toArPosition(to, boundingBox!!)

    currentPieces[to] = Pair(rank, model)
  }

  fun removeOldPiece(pieces: Map<Position, Piece>) {

    val removed = currentPieces.filter { !pieces.containsValue(it.value.first) }
    Log.d(TAG, "removed $removed")

    for ((key, value) in removed) {
      boardNode.removeChild(value.second)
      value.second.destroy()
      currentPieces.remove(key)
    }
  }

  suspend fun replaceModel(from: Position, to: Position, rank: Rank) {
    /*    val (piece, model) = currentPieces[from] ?: return
    currentPieces.remove(from)

    model.position = toArPosition(to, boundingBox!!)

    model.setModel(loadPieceRenderable(rank))
    val newPiece = Piece()
    newPiece.rank = rank

    currentPieces[to] = Pair(newPiece, model)*/
  }

  /** Scale the whole scene with the given [value] */
  internal fun scale(value: Float) {
    boardNode.scale(value)
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
fun toArPosition(
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
