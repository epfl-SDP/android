package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.min
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * A composable which displays a chess board based on some given state. Each piece is disambiguated
 * using its [Identifier], so pieces smoothly animate when the board changes.
 *
 * @param Identifier the type of the piece identifiers.
 * @param state the [ChessBoardState] that is used by this composable.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <Identifier> ChessBoard(
    state: ChessBoardState<Identifier>,
    modifier: Modifier = Modifier,
) {
  BoxWithConstraints(
      modifier
          .aspectRatio(1f)
          .checkerboard(
              cells = 8,
              color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.disabled),
          )
          .grid(cells = 8, color = MaterialTheme.colors.primary),
  ) {
    val minDimension = with(LocalDensity.current) { min(maxHeight, maxWidth).toPx() }
    val cellPx = minDimension / 8
    val cellDp = with(LocalDensity.current) { cellPx.toDp() }
    val scope = rememberCoroutineScope()

    for ((position, piece) in state.pieces) {
      key(piece) {
        val currentTarget by rememberUpdatedState(Offset(cellPx * position.x, cellPx * position.y))
        val currentTargetAnimatable = remember { Animatable(currentTarget, Offset.VectorConverter) }

        val currentPosition by rememberUpdatedState(position)
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
                    .pointerInput(Unit) {
                      detectDragGestures(
                          onDragStart = {
                            draggingState.offset = currentTarget
                            draggingState.isDragging = true
                          },
                          onDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            draggingState.offset += dragAmount
                          },
                          onDragEnd = {
                            scope.launch {
                              // Set the current dragging state to false, so the LaunchedEffect is
                              // triggered and the offset is animated to the right target position.
                              draggingState.isDragging = false
                              currentTargetAnimatable.snapTo(draggingState.offset)

                              val (x, y) = draggingState.offset / cellPx
                              state.onDropPiece(
                                  piece = piece,
                                  startPosition = currentPosition,
                                  endPosition =
                                      Position(
                                          x.roundToInt().coerceIn(0, 7),
                                          y.roundToInt().coerceIn(0, 7),
                                      ),
                              )
                            }
                          },
                          onDragCancel = { draggingState.isDragging = false },
                      )
                    }
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
 * Draws a single [Piece] of chess.
 *
 * @param piece the piece to be drawn.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun Piece(
    piece: Piece<*>,
    modifier: Modifier = Modifier,
) {
  Icon(
      painter = pieceIcon(piece),
      contentDescription = "${piece.color} ${piece.rank}",
      modifier = modifier,
  )
}

/** Returns the [Painter] which should be used to draw the given [piece]. */
@Composable
private fun pieceIcon(piece: Piece<*>): Painter =
    when (piece.color) {
      Black ->
          when (piece.rank) {
            King -> ChessIcons.BlackKing
            Queen -> ChessIcons.BlackQueen
            Rook -> ChessIcons.BlackRook
            Bishop -> ChessIcons.BlackBishop
            Knight -> ChessIcons.BlackKnight
            Pawn -> ChessIcons.BlackPawn
          }
      White ->
          when (piece.rank) {
            King -> ChessIcons.WhiteKing
            Queen -> ChessIcons.WhiteQueen
            Rook -> ChessIcons.WhiteRook
            Bishop -> ChessIcons.WhiteBishop
            Knight -> ChessIcons.WhiteKnight
            Pawn -> ChessIcons.WhitePawn
          }
    }
