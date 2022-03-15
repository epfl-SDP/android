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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.Piece as GamePiece
import ch.epfl.sdp.mobile.application.chess.Position as GamePosition
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import kotlin.math.roundToInt

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

  // TODO: PieceIdentifier should be this interface's own type
  data class Piece(val id: PieceIdentifier, val rank: Rank, val color: Color)

  val pieces: Map<Position, Piece>

  val dragEnabled: Boolean

  fun onDropPiece(piece: Piece, startPosition: Position, endPosition: Position)
}

fun GamePosition.toPosition(): Position {
  return Position(this.x, this.y)
}

fun GamePiece.toPiece(): Piece {
  val rank =
      when (this.rank) {
        Rank.King -> King
        Rank.Queen -> Queen
        Rank.Rook -> Rook
        Rank.Bishop -> Bishop
        Rank.Knight -> Knight
        Rank.Pawn -> Pawn
      }

  val color =
      when (this.color) {
        ch.epfl.sdp.mobile.application.chess.Color.Black -> Black
        ch.epfl.sdp.mobile.application.chess.Color.White -> White
      }

  return Piece(id = this.id, rank = rank, color = color)
}

class FakeChessBoardState() : ChessBoardState {
  private var game by mutableStateOf(emptyGame())

  override val pieces: Map<Position, Piece>
    get() =
        GamePosition.all()
            .map { game.board[it]?.let { p -> it to p } }
            .filterNotNull()
            .toMap()
            .map { (a, b) -> a.toPosition() to b.toPiece() }
            .toMap()

  override val dragEnabled: Boolean
    get() = true // TODO: Change me!

  override fun onDropPiece(piece: Piece, startPosition: Position, endPosition: Position) {
    val step = game.nextStep as NextStep.MovePiece
    game =
        step.move(
            GamePosition(startPosition.x, startPosition.y),
            Delta(endPosition.x - startPosition.x, endPosition.y - startPosition.y))
  }
}

@Composable
fun rememberChessBoardState(): ChessBoardState {
  return remember { FakeChessBoardState() }
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
