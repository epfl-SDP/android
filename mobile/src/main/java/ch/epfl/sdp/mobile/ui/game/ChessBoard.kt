package ch.epfl.sdp.mobile.ui.game

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

    for ((position, piece) in state.pieces) {
      key(piece) {
        val currentTarget by rememberUpdatedState(Offset(cellPx * position.x, cellPx * position.y))

        val currentPosition by rememberUpdatedState(position)
        val draggingState = remember {
          DraggingState(
              mutableStateOf(false),
              mutableStateOf(currentTarget),
          )
        }

        Piece(
            piece = piece,
            modifier =
                Modifier.offset {
                      if (draggingState.isDragging)
                          IntOffset(
                              draggingState.offset.x.roundToInt(),
                              draggingState.offset.y.roundToInt(),
                          )
                      else {
                        IntOffset(
                            currentTarget.x.roundToInt(),
                            currentTarget.y.roundToInt(),
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
                            draggingState.isDragging = false
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

class DraggingState(
    isDragging: MutableState<Boolean>,
    offset: MutableState<Offset>,
) {
  var isDragging: Boolean by isDragging
  var offset: Offset by offset
}

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
