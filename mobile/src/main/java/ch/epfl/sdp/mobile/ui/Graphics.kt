package ch.epfl.sdp.mobile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import ch.epfl.sdp.mobile.R

/** Returns the [PawniesIcons] from which Pawnies icons are queried. */
val Icons.Branded: PawniesIcons
  get() = PawniesIcons

/** An object that contains all the icons that will be used in Pawnies. */
object PawniesIcons

val PawniesIcons.ArView
  get() = Icons.Default.ViewInAr

/** An icon to close the game screen. */
val PawniesIcons.GameClose
  get() = Icons.Rounded.Close

/** An icon that indicates that the mic if on. */
val PawniesIcons.GameMicOn
  get() = Icons.Default.Mic

/** An icon that indicates that the mic is off. */
val PawniesIcons.GameMicOff
  get() = Icons.Default.MicOff

/** An icon to show a password. */
val PawniesIcons.PasswordShow
  get() = Icons.Default.Visibility

/** An icon to hide a password. */
val PawniesIcons.PasswordHide
  get() = Icons.Default.VisibilityOff

/** An icon for the social section. */
val PawniesIcons.SectionSocial
  get() = Icons.Default.People

/** An icon for the settings section. */
val PawniesIcons.SectionSettings
  get() = Icons.Default.Settings

/** An icon for the chess board section. */
val PawniesIcons.SectionPlay
  get() = Icons.Default.Games

val PawniesIcons.Add
  get() = Icons.Default.Add

val PawniesIcons.Search
  get() = Icons.Default.Search

val PawniesIcons.Check
  get() = Icons.Default.Check

val PawniesIcons.LocalPlay
  @Composable get() = painterResource(id = R.drawable.local_play)

val PawniesIcons.OnlinePlay
  @Composable get() = painterResource(id = R.drawable.online_play)

val PawniesIcons.Close
  get() = Icons.Default.Close

/** Chess pieces */
object ChessIcons

val ChessIcons.WhiteKing
  @Composable get() = painterResource(id = R.drawable.ic_chess_king_white)

val ChessIcons.WhiteQueen
  @Composable get() = painterResource(id = R.drawable.ic_chess_queen_white)

val ChessIcons.WhiteRook
  @Composable get() = painterResource(id = R.drawable.ic_chess_rook_white)

val ChessIcons.WhiteBishop
  @Composable get() = painterResource(id = R.drawable.ic_chess_bishop_white)

val ChessIcons.WhiteKnight
  @Composable get() = painterResource(id = R.drawable.ic_chess_knight_white)

val ChessIcons.WhitePawn
  @Composable get() = painterResource(id = R.drawable.ic_chess_pawn_white)

val ChessIcons.BlackKing
  @Composable get() = painterResource(id = R.drawable.ic_chess_king_black)

val ChessIcons.BlackQueen
  @Composable get() = painterResource(id = R.drawable.ic_chess_queen_black)

val ChessIcons.BlackRook
  @Composable get() = painterResource(id = R.drawable.ic_chess_rook_black)

val ChessIcons.BlackBishop
  @Composable get() = painterResource(id = R.drawable.ic_chess_bishop_black)

val ChessIcons.BlackKnight
  @Composable get() = painterResource(id = R.drawable.ic_chess_knight_black)

val ChessIcons.BlackPawn
  @Composable get() = painterResource(id = R.drawable.ic_chess_pawn_black)
