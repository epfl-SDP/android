package ch.epfl.sdp.mobile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import ch.epfl.sdp.mobile.R

/** Returns the [PawniesIcons] from which Pawnies icons are queried. */
val Icons.Branded: PawniesIcons
  get() = PawniesIcons

/** An object that contains all the icons that will be used in Pawnies. */
object PawniesIcons

/** An icon to indicate the AR section. */
val PawniesIcons.ArView
  get() = Icons.Default.ViewInAr

/** An icon to close the game screen. */
val PawniesIcons.Close
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

val PawniesIcons.Delete
  get() = Icons.Default.Delete


/** An icon for the social section. */
val PawniesIcons.SectionSocialSelected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_friends_filled)

/** An unselected icon for the social section. */
val PawniesIcons.SectionSocialUnselected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_friends_hollow)

/** A selected icon for the settings section. */
val PawniesIcons.SectionSettingsSelected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_settings_filled)

/** An unselected icon for the settings section. */
val PawniesIcons.SectionSettings
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_settings_hollow)

/** A selected icon for the chess board section. */
val PawniesIcons.SectionPlaySelected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_play_filled)

/** An unselected icon for the chess board section. */
val PawniesIcons.SectionPlayUnselected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_play_hollow)

/** An selected icon for the puzzles section. */
val PawniesIcons.SectionPuzzlesSelected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_puzzles_filled)

/** An unselected icon for the puzzles section. */
val PawniesIcons.SectionPuzzlesUnselected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_puzzles_hollow)

/** A selected icon for the tournament section. */
val PawniesIcons.SectionContestsSelected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_tournaments_filled)

/** An unselected icon for the tournament section. */
val PawniesIcons.SectionContestsUnselected
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_tournaments_hollow)

/** An icon for the add action. */
val PawniesIcons.Add
  get() = Icons.Default.Add

/** An icon for search. */
val PawniesIcons.Search
  get() = Icons.Default.Search

/** An icon for checkmarks. */
val PawniesIcons.Check
  get() = Icons.Default.Check

/** An icon for creating a local game. */
val PawniesIcons.LocalPlay
  @Composable get() = painterResource(id = R.drawable.ic_local_play)

/** An icon for creating an online game. */
val PawniesIcons.OnlinePlay
  @Composable get() = painterResource(id = R.drawable.ic_online_play)

/** An icon to filter results. */
val PawniesIcons.Filter
  @Composable get() = painterResource(id = R.drawable.ic_filter)

/** An icon to close some tournament details. */
val PawniesIcons.TournamentDetailsClose
  @Composable get() = Icons.Rounded.Close

/** An icon to indicate the next step of a tournament. */
val PawniesIcons.TournamentsNextStep
  get() = Icons.Outlined.SkipNext

/** An icon to log out. */
val PawniesIcons.Logout
  get() = Icons.Default.Logout

/** An icon to open some settings. */
val PawniesIcons.Settings
  @Composable get() = painterResource(id = R.drawable.ic_tab_icons_settings_hollow)

/** An icon to edit some settings. */
val PawniesIcons.Edit
  @Composable get() = painterResource(id = R.drawable.ic_edit)

/** Chess pieces. */
object ChessIcons

/** The white king. */
val ChessIcons.WhiteKing
  @Composable get() = painterResource(id = R.drawable.ic_chess_king_white)

/** The white queen. */
val ChessIcons.WhiteQueen
  @Composable get() = painterResource(id = R.drawable.ic_chess_queen_white)

/** The white rook. */
val ChessIcons.WhiteRook
  @Composable get() = painterResource(id = R.drawable.ic_chess_rook_white)

/** The white bishop. */
val ChessIcons.WhiteBishop
  @Composable get() = painterResource(id = R.drawable.ic_chess_bishop_white)

/** The white knight. */
val ChessIcons.WhiteKnight
  @Composable get() = painterResource(id = R.drawable.ic_chess_knight_white)

/** The white pawn. */
val ChessIcons.WhitePawn
  @Composable get() = painterResource(id = R.drawable.ic_chess_pawn_white)

/** The black king. */
val ChessIcons.BlackKing
  @Composable get() = painterResource(id = R.drawable.ic_chess_king_black)

/** The black queen. */
val ChessIcons.BlackQueen
  @Composable get() = painterResource(id = R.drawable.ic_chess_queen_black)

/** The black rook. */
val ChessIcons.BlackRook
  @Composable get() = painterResource(id = R.drawable.ic_chess_rook_black)

/** The black bishop. */
val ChessIcons.BlackBishop
  @Composable get() = painterResource(id = R.drawable.ic_chess_bishop_black)

/** The black knight. */
val ChessIcons.BlackKnight
  @Composable get() = painterResource(id = R.drawable.ic_chess_knight_black)

/** The black pawn. */
val ChessIcons.BlackPawn
  @Composable get() = painterResource(id = R.drawable.ic_chess_pawn_black)
