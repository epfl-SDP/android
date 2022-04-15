package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.min
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import kotlin.math.roundToInt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** The width (and height) in number of cells of a ChessBoard */
const val ChessBoardCells = 8

/**
 * A composable which displays a chess board based on some given state. Each piece is disambiguated
 * using its unique [Piece] type, so pieces smoothly animate when the board changes.
 *
 * @param Piece the type of the pieces.
 * @param state the [MovableChessBoardState] that is used by this composable.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true iff the [MovableChessBoardState] should allow for user interactions.
 */
@Composable
fun <Piece : ChessBoardState.Piece> ClassicChessBoard(
    state: MovableChessBoardState<Piece>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
  val strings = LocalLocalizedStrings.current

  BoxWithConstraints(
      modifier
          .aspectRatio(1f)
          .checkerboard(
              cells = ChessBoardCells,
              color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.disabled),
          )
          .check(
              position = state.checkPosition,
              color = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.medium),
          )
          .grid(cells = ChessBoardCells, color = MaterialTheme.colors.primary)
          .actions(
              positions = state.availableMoves,
              color = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.disabled),
          )
          .selection(
              position = state.selectedPosition,
              color = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.medium),
          )
          .semantics { this.contentDescription = strings.boardContentDescription },
  ) {
    val minDimension = with(LocalDensity.current) { min(maxHeight, maxWidth).toPx() }
    val cellPx = minDimension / ChessBoardCells
    val cellDp = with(LocalDensity.current) { cellPx.toDp() }
    val scope = rememberCoroutineScope()

    // Detect the tap gestures on the board. This won't detect tap gestures on the pieces however.
    Box(
        Modifier.fillMaxSize().pointerInput(state, enabled) {
          if (enabled) {
            detectTapGestures { offset ->
              val position = offset / cellPx
              state.onPositionClick(Position(position.x.toInt(), position.y.toInt()))
            }
          }
        },
    )

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
            currentTargetAnimatable.animateTo(currentTarget, spring(stiffness = StiffnessLow))
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
                            Modifier.movablePiece(
                                state = draggingState,
                                target = currentTarget,
                                cellSize = cellPx,
                                onClick = { state.onPositionClick((position)) },
                                onDrop = { droppedPosition ->
                                  scope.launch {
                                    currentTargetAnimatable.snapTo(draggingState.offset)
                                    state.onDropPiece(piece, droppedPosition)
                                  }
                                },
                            )
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
 * @param onClick the callback called when the piece is clicked.
 * @param onDrop the callback called when the piece is dropped.
 */
private fun Modifier.movablePiece(
    state: DraggingState,
    target: Offset,
    cellSize: Float,
    onClick: () -> Unit,
    onDrop: (Position) -> Unit,
): Modifier = composed {
  val currentTarget by rememberUpdatedState(target)
  val currentOnClick by rememberUpdatedState(onClick)
  val currentOnDrop by rememberUpdatedState(onDrop)
  pointerInput(Unit) {
    coroutineScope {
      launch { detectTapGestures { currentOnClick() } }
      launch {
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
  Icon(
      painter = piece.icon,
      contentDescription = piece.contentDescription,
      modifier = modifier,
  )
}

/** Returns the [Painter] associated to the value of this [Piece]. */
private val Piece.icon: Painter
  @Composable
  get() =
      when (color) {
        Black ->
            when (rank) {
              King -> ChessIcons.BlackKing
              Queen -> ChessIcons.BlackQueen
              Rook -> ChessIcons.BlackRook
              Bishop -> ChessIcons.BlackBishop
              Knight -> ChessIcons.BlackKnight
              Pawn -> ChessIcons.BlackPawn
            }
        White ->
            when (rank) {
              King -> ChessIcons.WhiteKing
              Queen -> ChessIcons.WhiteQueen
              Rook -> ChessIcons.WhiteRook
              Bishop -> ChessIcons.WhiteBishop
              Knight -> ChessIcons.WhiteKnight
              Pawn -> ChessIcons.WhitePawn
            }
      }

/** Return the associated content description [String] for [ChessBoardState.Color] */
fun ChessBoardState.Color.contentDescription(strings: LocalizedStrings): String {
  return when (this) {
    Black -> strings.boardColorBlack
    White -> strings.boardColorWhite
  }
}

/** Return the associated content description [String] for [ChessBoardState.Rank] */
fun ChessBoardState.Rank.contentDescription(strings: LocalizedStrings): String {
  return when (this) {
    King -> strings.boardPieceKing
    Queen -> strings.boardPieceQueen
    Rook -> strings.boardPieceRook
    Bishop -> strings.boardPieceBishop
    Knight -> strings.boardPieceKnight
    Pawn -> strings.boardPiecePawn
  }
}

/** Returns the [String] content description associated to the value of this [Piece]. */
private val Piece.contentDescription: String
  @Composable
  get() {
    val strings = LocalLocalizedStrings.current
    val color = color.contentDescription(strings)
    val rank = rank.contentDescription(strings)
    return strings.boardPieceContentDescription(color, rank)
  }