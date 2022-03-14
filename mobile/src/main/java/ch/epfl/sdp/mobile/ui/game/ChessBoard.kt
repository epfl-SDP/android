package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.flowOf

val firstState =
    mapOf(
        Position(0, 1) to Piece(id = 1, rank = King, color = White),
        Position(1, 2) to Piece(id = 2, rank = Queen, color = White),
        Position(2, 3) to Piece(id = 3, rank = Rook, color = White),
        Position(3, 4) to Piece(id = 4, rank = Bishop, color = Black),
        Position(4, 5) to Piece(id = 5, rank = Knight, color = Black),
        Position(5, 6) to Piece(id = 6, rank = Pawn, color = Black),
    )

val secondState =
    mapOf(
        Position(1, 3) to Piece(id = 3, rank = Rook, color = White),
        Position(6, 6) to Piece(id = 6, rank = Pawn, color = Black),
        Position(3, 2) to Piece(id = 2, rank = Queen, color = White),
        Position(1, 5) to Piece(id = 1, rank = King, color = White),
        Position(7, 2) to Piece(id = 5, rank = Knight, color = Black),
        Position(4, 5) to Piece(id = 4, rank = Bishop, color = Black),
    )

val state = flowOf(firstState)

@Stable
interface ChessBoardState {
  enum class Rank {
    King,
    Queen,
    Rook,
    Bishop,
    Knight,
    Pawn,
  }

  data class Position(val x: Int, val y: Int)

  enum class Color {
    Black,
    White,
  }

  data class Piece(val id: Int, val rank: Rank, val color: Color)

  val pieces: Map<Position, Piece>

  val dragEnabled: Boolean

  fun onDropPiece(piece: Piece, position: Position)
}

class FakeChessBoardState(override val pieces: Map<Position, Piece>) : ChessBoardState {

  override val dragEnabled: Boolean
    get() = TODO("Not yet implemented")

  override fun onDropPiece(piece: Piece, position: Position) {
    TODO("Not yet implemented")
  }
}

@Composable
fun rememberChessBoardState(): ChessBoardState {
  val pieces by state.collectAsState(initial = emptyMap())
  return FakeChessBoardState(pieces)
}

@Composable
fun ChessBoard(
    state: ChessBoardState = rememberChessBoardState(),
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

    for ((position, piece) in state.pieces) {
      key(piece) {
        val x by animateFloatAsState(position.x.toFloat(), spring(stiffness = StiffnessMediumLow))
        val y by animateFloatAsState(position.y.toFloat(), spring(stiffness = StiffnessMediumLow))
        val offset = remember { mutableStateOf(Offset.Zero) }
        Piece(
          piece = piece,
          modifier =
          Modifier
            .offset {
              IntOffset(
                (squareSizeDp * x).roundToPx(),
                (squareSizeDp * y).roundToPx(),
              )
            }
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .draggablePiece(offset = offset, onDrop = { offset.value = Offset.Zero })
            .size(squareSizeDp),
        )
      }
    }
  }
}

fun Modifier.draggablePiece(offset: MutableState<Offset>, onDrop: () -> Unit): Modifier =
    pointerInput(Unit) {
      detectDragGestures(
          onDragStart = { offset.value = Offset.Zero },
          onDrag = { change, dragAmount ->
            change.consumeAllChanges()
            offset.value += dragAmount
          },
          onDragEnd = onDrop,
      )
    }

@Composable
private fun pieceIcon(piece: Piece): Painter =
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
fun Piece(piece: Piece, modifier: Modifier = Modifier) {
  Icon(
      painter = pieceIcon(piece),
      contentDescription = null,
      modifier = modifier,
  )
}

fun Modifier.checkerboard(
    color: Color = Color.Unspecified,
    cells: Int = 8,
): Modifier = composed {
  val squareColor = color.takeOrElse { LocalContentColor.current }
  drawBehind {
    val origin = size.center - Offset(size.minDimension / 2, size.minDimension / 2)
    val squareSize = size.minDimension / cells

    for (i in 0 until cells) {
      for (j in 0 until cells) {
        val squareOffset = origin + Offset(i * squareSize, j * squareSize)
        if ((i + j) % 2 == 1) {
          drawRect(
              color = squareColor,
              topLeft = squareOffset,
              size = Size(squareSize, squareSize),
          )
        }
      }
    }
  }
}

fun Modifier.grid(
    color: Color = Color.Unspecified,
    width: Dp = 2.dp,
    cells: Int = 8,
): Modifier = composed {
  val lineColor = color.takeOrElse { LocalContentColor.current }
  drawBehind {
    val origin = size.center - Offset(size.minDimension / 2, size.minDimension / 2)
    val squareSize = size.minDimension / cells

    for (i in 0..cells) {
      drawLine(
          color = lineColor,
          start = origin + Offset(i * squareSize, 0f),
          end = origin + Offset(i * squareSize, size.minDimension),
          strokeWidth = width.toPx(),
          cap = StrokeCap.Round,
      )
      drawLine(
          color = lineColor,
          start = origin + Offset(0f, i * squareSize),
          end = origin + Offset(size.minDimension, i * squareSize),
          strokeWidth = width.toPx(),
          cap = StrokeCap.Round,
      )
    }
  }
}
