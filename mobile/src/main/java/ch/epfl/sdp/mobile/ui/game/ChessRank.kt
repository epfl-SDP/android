package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/** The different ranks which may be displayed by a [ChessBoard]. */
interface ChessRank

enum class ClassicRank(
    val contentDescription: LocalizedStrings.() -> String,
    val whiteIcon: @Composable () -> Painter,
    val blackIcon: @Composable () -> Painter,
) : ChessRank {
  King(
      contentDescription = { boardPieceKing },
      whiteIcon = { ChessIcons.WhiteKing },
      blackIcon = { ChessIcons.BlackKing },
  ),
  Queen(
      contentDescription = { boardPieceQueen },
      whiteIcon = { ChessIcons.WhiteQueen },
      blackIcon = { ChessIcons.BlackQueen },
  ),
  Rook(
      contentDescription = { boardPieceRook },
      whiteIcon = { ChessIcons.WhiteRook },
      blackIcon = { ChessIcons.BlackRook },
  ),
  Bishop(
      contentDescription = { boardPieceBishop },
      whiteIcon = { ChessIcons.WhiteBishop },
      blackIcon = { ChessIcons.BlackBishop },
  ),
  Knight(
      contentDescription = { boardPieceKnight },
      whiteIcon = { ChessIcons.WhiteKnight },
      blackIcon = { ChessIcons.BlackKnight },
  ),
  Pawn(
      contentDescription = { boardPiecePawn },
      whiteIcon = { ChessIcons.WhitePawn },
      blackIcon = { ChessIcons.BlackPawn },
  )
}