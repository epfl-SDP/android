package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import ch.epfl.sdp.mobile.application.chess.*
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
          .padding(16.dp)
          .aspectRatio(1f)
          .checkerboard(
              cells = 8,
              color = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.disabled),
          )
          .grid(cells = 8, color = MaterialTheme.colors.primary),
  ) {
    val minDimension = min(this.maxHeight, this.maxWidth)
    val squareSizeDp = minDimension / 8
    val density = LocalDensity.current

    for ((position, piece) in state.pieces) {
      key(piece) {
        val targetX = squareSizeDp * position.x
        val targetY = squareSizeDp * position.y

        val x by animateDpAsState(targetX, spring(stiffness = StiffnessMediumLow))
        val y by animateDpAsState(targetY, spring(stiffness = StiffnessMediumLow))

        val offset = remember { mutableStateOf(Offset.Zero) }

        val offsetX by animateFloatAsState(offset.value.x, spring(stiffness = StiffnessHigh))
        val offsetY by animateFloatAsState(offset.value.y, spring(stiffness = StiffnessHigh))

        Piece(
            piece = piece,
            modifier =
                Modifier.offset { IntOffset(x.roundToPx(), y.roundToPx()) }
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .draggablePiece(
                        offset = offset,
                        onDrop = {
                          val cellX = targetX + with(density) { offset.value.x.toDp() }
                          val cellY = targetY + with(density) { offset.value.y.toDp() }

                          state.onDropPiece(
                              piece = piece,
                              startPosition = position,
                              endPosition =
                                  Position(
                                      (cellX / squareSizeDp).roundToInt().coerceIn(0, 7),
                                      (cellY / squareSizeDp).roundToInt().coerceIn(0, 7),
                                  ),
                          )
                          offset.value = Offset.Zero
                        })
                    .size(squareSizeDp),
        )
      }
    }
  }
}

fun Modifier.draggablePiece(
    offset: MutableState<Offset>,
    onDrop: () -> Unit,
): Modifier = composed {
  val currentOnDrop by rememberUpdatedState(onDrop)
  pointerInput(Unit) {
    detectDragGestures(
        onDragStart = { offset.value = Offset.Zero },
        onDrag = { change, dragAmount ->
          change.consumeAllChanges()
          offset.value += dragAmount
        },
        onDragEnd = { currentOnDrop() },
    )
  }
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

@Composable
fun Piece(piece: Piece<*>, modifier: Modifier = Modifier) {
  Icon(
      painter = pieceIcon(piece),
      contentDescription = null,
      modifier = modifier,
  )
}
