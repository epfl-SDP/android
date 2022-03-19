package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.min
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** The width (and height) in number of cells of a ChessBoard */
const val ChessBoardCells = 8

/**
 * A composable which displays a chess board based on some given state. Each piece is disambiguated
 * using its unique [Piece] type, so pieces smoothly animate when the board changes.
 *
 * @param Piece the type of the pieces.
 * @param state the [ChessBoardState] that is used by this composable.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true iff the [ChessBoardState] should allow for user interactions.
 */
@Composable
fun <Piece : ChessBoardState.Piece> ChessBoard(
    state: ChessBoardState<Piece>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
  BoxWithConstraints(
      modifier
          .aspectRatio(1f)
          .checkerboard(
              cells = ChessBoardCells,
              color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.disabled),
          )
          .grid(cells = ChessBoardCells, color = MaterialTheme.colors.primary)
          .actions(
              positions = state.availableMoves,
              color = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.disabled),
          ),
  ) {
    val minDimension = with(LocalDensity.current) { min(maxHeight, maxWidth).toPx() }
    val cellPx = minDimension / ChessBoardCells
    val cellDp = with(LocalDensity.current) { cellPx.toDp() }
    val scope = rememberCoroutineScope()

    for ((position, piece) in state.pieces) {
      key(piece) {
        val currentTarget by rememberUpdatedState(Offset(cellPx * position.x, cellPx * position.y))
        val currentTargetAnimatable = remember { Animatable(currentTarget, Offset.VectorConverter) }

        val draggingState = remember {
          DraggingState(
              mutableStateOf(false),
              mutableStateOf(currentTarget),
          )
        }

        // When the current target is changed, we'll try to animate the currentTargetAnimatable
        // towards its value. However, if we're currently dragging, we won't play the animatable.
        LaunchedEffect(currentTarget, draggingState.isDragging) {
          if (!draggingState.isDragging) {
            currentTargetAnimatable.animateTo(currentTarget)
          }
        }

        Piece(
            piece = piece,
            modifier =
                Modifier.offset {
                      if (draggingState.isDragging) {
                        IntOffset(
                            draggingState.offset.x.roundToInt(),
                            draggingState.offset.y.roundToInt(),
                        )
                      } else {
                        val offset by currentTargetAnimatable.asState()
                        IntOffset(
                            offset.x.roundToInt(),
                            offset.y.roundToInt(),
                        )
                      }
                    }
                    .then(
                        if (enabled)
                            Modifier.movablePiece(draggingState, currentTarget, cellPx) {
                                droppedPosition ->
                              scope.launch {
                                currentTargetAnimatable.snapTo(draggingState.offset)
                                state.onDropPiece(piece, droppedPosition)
                              }
                            }
                        else Modifier,
                    )
                    .size(cellDp),
        )
      }
    }
  }
}

/** A holder for the drag state of an individual piece. */
private class DraggingState(
    isDragging: MutableState<Boolean>,
    offset: MutableState<Offset>,
) {
  var isDragging: Boolean by isDragging
  var offset: Offset by offset
}

/**
 * A custom [Modifier] which records drag gestures to the provided [DraggingState], and consumes the
 * changes as needed. When the drag gesture starts, the [DraggingState] will be set to [target],
 * which indicates the starting position of the piece. Once dropped, [onDrop] is called with the
 * drop position, computed using the [cellSize].
 *
 * @param state the [DraggingState] for this composable.
 * @param target the [Offset] at which the piece rests.
 * @param cellSize the size of each square / piece of the board.
 * @param onDrop the callback called when the piece is dropped.
 */
private fun Modifier.movablePiece(
    state: DraggingState,
    target: Offset,
    cellSize: Float,
    onDrop: (Position) -> Unit,
): Modifier = composed {
  val currentTarget by rememberUpdatedState(target)
  val currentOnDrop by rememberUpdatedState(onDrop)
  pointerInput(Unit) {
    detectDragGestures(
        onDragStart = {
          state.offset = currentTarget
          state.isDragging = true
        },
        onDrag = { change, dragAmount ->
          change.consumeAllChanges()
          state.offset += dragAmount
        },
        onDragEnd = {
          state.isDragging = false
          val (x, y) = state.offset / cellSize
          currentOnDrop(Position(x.roundToInt(), y.roundToInt()))
        },
        onDragCancel = { state.isDragging = false },
    )
  }
}

/**
 * Draws a single [Piece] of chess.
 *
 * @param piece the piece to be drawn.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun Piece(
    piece: Piece,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val painter =
      when (piece.color) {
        Black -> piece.rank.blackIcon
        White -> piece.rank.whiteIcon
      }
  val contentDescription =
      strings.boardPieceContentDescription(
          piece.color.contentDescription(strings),
          piece.rank.contentDescription(strings),
      )
  Icon(
      painter = painter(),
      contentDescription = contentDescription,
      modifier = modifier,
  )
}
