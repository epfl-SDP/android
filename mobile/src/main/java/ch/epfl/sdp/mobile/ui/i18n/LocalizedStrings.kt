package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.runtime.Stable

/**
 * An interface which defines all the texts and content descriptions which are used in the Pawnies
 * application.
 */
@Stable
interface LocalizedStrings {
  val authenticateTitle: String
  val authenticateSubtitleRegister: String
  val authenticateSubtitleLogIn: String
  val authenticateEmailHint: String
  val authenticateNameHint: String
  val authenticatePasswordHint: String
  val authenticatePasswordToggleVisibility: String
  val authenticatePerformLogIn: String
  val authenticatePerformRegister: String
  val authenticateSwitchToLogIn: String
  val authenticateSwitchToRegister: String
  val authenticateErrorFailure: String
  val authenticateBadPasswordFailure: String
  val authenticateWrongEmailFormatFailure: String
  val authenticateInvalidUserFailure: String
  val authenticateIncorrectPasswordFailure: String
  val authenticateExistingAccountFailure: String
  val authenticateOr: String

  val arContentDescription: String

  val profileMatchTitle: (String) -> String
  val profileWonByCheckmate: (Int) -> String
  val profileWonByForfeit: (Int) -> String
  val profileLostByCheckmate: (Int) -> String
  val profileLostByForfeit: (Int) -> String
  val profileTieInfo: (Int) -> String
  val profilePastGames: String
  val profilePuzzle: String
  val profileSettings: String
  val profileUnfollow: String
  val profileChallenge: String
  val profileEditIcon: String
  val profileAgainst: String
  val profileYourTurn: String
  val profileOthersTurn: String

  val boardColorBlack: String
  val boardColorWhite: String
  val boardPieceKing: String
  val boardPieceQueen: String
  val boardPieceRook: String
  val boardPieceBishop: String
  val boardPieceKnight: String
  val boardPiecePawn: String
  val boardPieceContentDescription: (color: String, rank: String) -> String
  val boardContentDescription: String

  val gameBack: String
  val gameShowAr: String
  val gameMicOffContentDescription: String
  val gameMicOnContentDescription: String
  val gameListening: String
  val gameMessageYourTurn: String
  val gameMessageCheck: String
  val gameMessageStalemate: String
  val gameMessageCheckmate: String

  val socialFollowingTitle: String
  val socialPerformPlay: String
  val socialPerformFollow: String
  val socialPerformUnfollow: String
  val socialSearchBarPlaceHolder: String
  val socialSearchEmptyTitle: String
  val socialSearchEmptySubtitle: String
  val socialSearchClearContentDescription: String

  val sectionAr: String
  val sectionSocial: String
  val sectionSettings: String
  val sectionPlay: String
  val newGame: String

  val prepareGameChooseColor: String
  val prepareGameChooseOpponent: String
  val prepareGameChooseGame: String
  val prepareGameWhiteColor: String
  val prepareGameBlackColor: String
  val prepareGamePlay: String
  val prepareGameCancel: String
  val prepareGameSelectOpponent: String
  val prepareGameSelectedOpponent: String
  val prepareGamePlayLocal: String
  val prepareGamePlayOnline: String

  val playOnlineGames: String
}
