package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.piece
import java.util.*

/**
 * An object which contains some utilities to transform FEN (Forsyth–Edwards Notation) notation into
 * games
 */
object FenNotation {

  const val nothingSymbol = '-'
  const val RowSeparatorSymbol = '/'
  const val FieldSeparatorSymbol = ' '

  data class ParserPiece(val nPieces: Int, val piece: Optional<Pair<Color, Rank>>)

  data class CastlingRights(
      val kingSideWhite: Boolean = false,
      val queenSideWhite: Boolean = false,
      val kingSideBlack: Boolean = false,
      val queenSideBlack: Boolean = false,
  )

  // TODO: Need to accommodate for whose turn to play, castling rights etc...
  data class BoardSnapshot(
      val board: Board<Piece<Color>>,
      val playing: Color,
      val castlingRights: CastlingRights,
      val enPassant: Position?,
      val halfMoveClock: Int,
      val fullMoveClock: Int,
  )

  fun parseFen(text: String): BoardSnapshot {
    val tokens = text.split(" ")
    val chessboardText = tokens[0]
    val colorText = tokens[1]
    val castlingRightsText = tokens[2]
    val enPassantText = tokens[3]
    val halfMoveClockText = tokens[4]
    val fullMoveClockText = tokens[5]

    val board =
        buildBoard<Piece<Color>> {
          val rows = chessboardText.split(RowSeparatorSymbol)
          var id = PieceIdentifier(0)

          for (y: Int in rows.indices) {
            var x = 0
            var char = 0
            while (x < Board.Size) {
              val parserPiece = piece.parse(rows[y][char].toString()).single().output
              println("DEBUG: ($x, $y): ${parserPiece.piece} (${parserPiece.nPieces})")
              if (parserPiece.piece.isPresent) {
                set(
                    Position(x, y),
                    Piece(parserPiece.piece.get().first, parserPiece.piece.get().second, id++))
              }
              x += parserPiece.nPieces
              char++
            }
          }
        }

    val color = Color.White
    val enPassant = null
    val halfMoveClock = 0
    val fullMoveClock = 0

    val castlingRights =
        CastlingRights(
            kingSideWhite = false,
            queenSideWhite = false,
            kingSideBlack = false,
            queenSideBlack = false,
        )

    return BoardSnapshot(
        board = board,
        playing = color,
        castlingRights = castlingRights,
        enPassant = enPassant,
        halfMoveClock = halfMoveClock,
        fullMoveClock = fullMoveClock,
    )
  }
}
