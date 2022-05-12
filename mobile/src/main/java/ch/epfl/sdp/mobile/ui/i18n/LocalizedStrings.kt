package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import kotlin.time.Duration

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
  val profileAgainst: String
  val profileEditImageIcon: String
  val profileEditNameIcon: String
  val profileYourTurn: String
  val profileOthersTurn: String

  val settingEditSave: String
  val settingEditCancel: String
  val settingProfileNameLabel: String
  val settingProfileImageLabel: String

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
  val gamePromoteTitle: String
  val gamePromoteConfirm: String

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
  val sectionContests: String
  val sectionPuzzles: String

  val newGame: String
  val newContest: String

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

  val puzzlePlayingAs: (color: String) -> String
  val puzzleUnsolvedPuzzles: String
  val puzzleListContentDescription: String

  val tournamentsBadgeJoin: String
  val tournamentsBadgeParticipant: String
  val tournamentsBadgeAdmin: String
  val tournamentsTableScore: String
  val tournamentsPoolStillOpen: String
  val tournamentsPoolRound: (current: Int, total: Int) -> String
  val tournamentsPoolStartNextRound: String
  val tournamentsDetailsFinals: String
  val tournamentsDetailsPools: String
  val tournamentsDetailsWatch: String
  val tournamentsDetailsMatchWon: String
  val tournamentsDetailsMatchLost: String
  val tournamentsDetailsMatchDrawn: String
  val tournamentsDetailsHeaderOngoing: String
  val tournamentsDetailsHeaderDone: String
  val tournamentsDetailsNextBestOfTitle: (Int, Int) -> String
  val tournamentsDetailsNextBestOfSubtitle: String
  val tournamentsDetailsNextRoundTitle: String
  val tournamentsDetailsNextRoundSubtitle: String
  val tournamentsDetailsStartEnoughPlayersTitle: String
  val tournamentsDetailsStartEnoughPlayersSubtitle: String
  val tournamentsDetailsStartNotEnoughPlayersTitle: String
  val tournamentsDetailsStartNotEnoughPlayersSubtitle: String
  val tournamentDetailsBackContentDescription: String

  val tournamentsContestsTitle: String
  val tournamentsFilter: String
  val tournamentsStartingTime: (Duration, SpanStyle) -> AnnotatedString
  val tournamentsDone: String
  val tournamentsCreateTitle: String
  val tournamentsCreateNameHint: String
  val tournamentsCreateRules: String
  val tournamentsCreateBestOf: String
  val tournamentsCreatePlayers: String
  val tournamentsCreateMaximumPlayerHint: String
  val tournamentsCreatePoolSize: String
  val tournamentsCreateDirectElimination: String
  val tournamentsCreateActionCancel: String
  val tournamentsCreateActionCreate: String

  val tournamentsCreateQualifierSize0: String
  val tournamentsCreateQualifierSizeN: (size: Int) -> String
  val tournamentsCreateElimDemomN: (denominator: Int) -> String
  val tournamentsCreateElimDepthFinal: String
}
