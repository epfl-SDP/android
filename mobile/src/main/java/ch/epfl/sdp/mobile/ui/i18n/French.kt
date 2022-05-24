package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/** Localized strings for the English language. */
object French : LocalizedStrings {
  override val authenticateTitle = "Pawnies"
  override val authenticateSubtitleRegister = "Join the Pawnies community.".uppercase()
  override val authenticateSubtitleLogIn = "Welcome back to Pawnies.".uppercase()
  override val authenticateEmailHint = "Your email"
  override val authenticateNameHint = "Your full name"
  override val authenticatePasswordHint = "Password"
  override val authenticatePasswordToggleVisibility = "Toggle password visibility"
  override val authenticatePerformLogIn = "Log in with email".uppercase()
  override val authenticatePerformRegister = "Sign up with email".uppercase()
  override val authenticateSwitchToLogIn = "Log in".uppercase()
  override val authenticateSwitchToRegister = "Sign up".uppercase()
  override val authenticateErrorFailure = "Something went wrong when authenticating"
  override val authenticateBadPasswordFailure = "Your password should be at least 6 characters long"
  override val authenticateWrongEmailFormatFailure = "Your email must be of the form: person@domain"
  override val authenticateInvalidUserFailure = "There is no user registered with this email"
  override val authenticateIncorrectPasswordFailure =
      "The email or password you entered is incorrect"
  override val authenticateExistingAccountFailure = "A user with this email already exists"
  override val authenticateOr = "or"

  override val arContentDescription: String = "ArComposable"

  override val boardColorBlack = "black"
  override val boardColorWhite = "white"
  override val boardPieceKing = "king"
  override val boardPieceQueen = "queen"
  override val boardPieceRook = "rook"
  override val boardPieceBishop = "bishop"
  override val boardPieceKnight = "knight"
  override val boardPiecePawn = "pawn"
  override val boardPieceContentDescription = { color: String, rank: String -> "$color $rank" }
  override val boardContentDescription = "chessboard"

  override val gameBack = "Back"
  override val gameShowAr = "Start AR mode"
  override val gameMicOffContentDescription = "Microphone off"
  override val gameMicOnContentDescription = "Microphone on"
  override val gameListening = "Listening"
  override val gameMessageYourTurn = "Your turn"
  override val gameMessageCheck = "Check !"
  override val gameMessageStalemate = "Stalemate !"
  override val gameMessageCheckmate = "Checkmate !"
  override val gamePromoteTitle = "Promote to:"
  override val gamePromoteConfirm = "Ok".uppercase()

  override val gameSnackBarIllegalAction: String = "Illegal action, please try again"
  override val gameSnackBarInternalFailure = "Internal failure"
  override val gameSnackBarUnknownCommand: String = "Unknown command, please try again"

  override val profileMatchTitle = { opponent: String -> "Against $opponent" }
  override val profileWonByCheckmate = { moves: Int -> "Won by checkmate after $moves moves" }
  override val profileWonByForfeit = { moves: Int -> "Won by forfeit after $moves moves" }
  override val profileLostByCheckmate = { moves: Int -> "Lost by checkmate after $moves moves" }
  override val profileLostByForfeit = { moves: Int -> "Lost by forfeit after $moves moves" }
  override val profileTieInfo = { moves: Int -> "Tie after $moves moves" }
  override val profileAgainst = "Against " // TODO: handle right-to-left languages
  override val profileYourTurn = "Your turn to play!"
  override val profileOthersTurn = "Their turn to play."

  override val settingEditSave = "Save".uppercase()
  override val settingEditCancel = "Cancel".uppercase()
  override val settingProfileNameLabel = "Profile Name"
  override val settingProfileImageLabel = "Profile Image"
  override val settingLogout: String = "Logout"
  override val settingLanguageLabel = "Language"

  override val profilePastGames = "Past Games".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileSettings = "Settings"
  override val profileFollow = "Follow"
  override val profileUnfollow = "Unfollow"
  override val profileChallenge = "Challenge"
  override val profileEditImageIcon = "Edit profile image icon"
  override val profileEditNameIcon = "Edit profile name icon"

  override val socialFollowingTitle = "Following"
  override val socialPerformPlay = "Play".uppercase()
  override val socialPerformFollow = "Follow".uppercase()
  override val socialPerformUnfollow = "Followed".uppercase()
  override val socialSearchBarPlaceHolder = "Search player ..."
  override val socialSearchEmptyTitle = "Search any player"
  override val socialSearchEmptySubtitle =
      "Find any player using their name, follow them, or invite them to play or see their match history"
  override val socialSearchClearContentDescription = "Clear search field"

  override val sectionAr: String = "AR"
  override val sectionSocial = "Players"
  override val sectionSettings = "Settings"
  override val sectionPlay = "Play"
  override val sectionPuzzles = "Puzzles"
  override val sectionContests = "Contests"

  override val newGame = "New game".uppercase()
  override val newContest = "New contest".uppercase()

  override val prepareGameChooseColor = "Pick your color :"
  override val prepareGameChooseOpponent = "Pick an opponent :"
  override val prepareGameChooseGame = "Pick your game :"
  override val prepareGameWhiteColor = "White".uppercase()
  override val prepareGameBlackColor = "Black".uppercase()
  override val prepareGamePlay = "Play !".uppercase()
  override val prepareGameCancel = "Cancel".uppercase()
  override val prepareGameSelectOpponent = "Select".uppercase()
  override val prepareGameSelectedOpponent = "Selected".uppercase()
  override val prepareGamePlayLocal = "Local".uppercase()
  override val prepareGamePlayOnline = "Online".uppercase()

  override val playOnlineGames = "Online games"

  override val puzzlePlayingAs = { color: String -> "Playing as $color" }
  override val puzzleUnsolvedPuzzles = "Unsolved puzzles"
  override val puzzleListContentDescription = "Puzzles list"
  override val puzzlesTitle = "Puzzles"
  override val puzzleSolving = { color: String -> "Find the best move for $color" }
  override val puzzleFailed = "You've failed! Try again!"
  override val puzzleSolved = "You won! Congrats!"
  override val puzzleNumber = { id: String -> "Puzzle: #$id" }
  override val puzzleRating = { rating: String -> "Rating: $rating" }

  override val tournamentsBadgeJoin = "Join"
  override val tournamentsBadgeParticipant = "Participant"
  override val tournamentsBadgeAdmin = "Admin"
  override val tournamentsTableScore = "Score"
  override val tournamentsPoolStillOpen = "Still open"
  override val tournamentsPoolRound = { current: Int, total: Int -> "Round $current / $total" }
  override val tournamentsPoolStartNextRound = "Start next round".uppercase()
  override val tournamentsDetailsFinals = "Finals".uppercase()
  override val tournamentsDetailsPools = "Pools".uppercase()
  override val tournamentsDetailsWatch = "Watch".uppercase()
  override val tournamentsDetailsMatchWon = "Win".uppercase()
  override val tournamentsDetailsMatchLost = "Loss".uppercase()
  override val tournamentsDetailsMatchDrawn = "Draw".uppercase()
  override val tournamentsDetailsHeaderOngoing = "Ongoing".uppercase()
  override val tournamentsDetailsHeaderDone = "Done".uppercase()
  override val tournamentsDetailsNextBestOfTitle = { round: Int, total: Int ->
    "Create match $round / $total"
  }
  override val tournamentsDetailsNextBestOfSubtitle =
      "Add a new match to all the players in this round."
  override val tournamentsDetailsNextRoundTitle = "Next round"
  override val tournamentsDetailsNextRoundSubtitle = "Move all the winners to the next round"
  override val tournamentsDetailsStartEnoughPlayersTitle = "Start the tournament"
  override val tournamentsDetailsStartEnoughPlayersSubtitle =
      "Once started, the tournament will assign players and the first matches will be created."
  override val tournamentsDetailsStartNotEnoughPlayersTitle = "Start the tournament"
  override val tournamentsDetailsStartNotEnoughPlayersSubtitle =
      "If you start the tournament now, there won't be enough players to play all the matches."
  override val tournamentsDetailsStartDirectEliminationTitle = "Start direct elimination"
  override val tournamentsDetailsStartDirectEliminationSubtitle =
      "Compute the pool results and start the direct elimination matches."
  override val tournamentDetailsBackContentDescription = "Back"
  override val tournamentDetailsPoolName = { poolNr: Int -> "Pool #${poolNr}" }

  override val tournamentsContestsTitle = "Contests"
  override val tournamentsFilter = "Filter"
  override val tournamentsStartingTime = { duration: Duration, style: SpanStyle ->
    buildAnnotatedString {
      append("Started ")
      withStyle(style) { append(duration.absoluteValue.toEnglishString()) }
      append(" ago")
    }
  }
  override val tournamentsDone = "Done"
  override val tournamentsCreateTitle = "Create tournament"
  override val tournamentsCreateNameHint = "Name"
  override val tournamentsCreateRules = "Rules"
  override val tournamentsCreateBestOf = "Best of :"
  override val tournamentsCreatePlayers = "Players"
  override val tournamentsCreateMaximumPlayerHint = "Maximum player count"
  override val tournamentsCreatePoolSize = "Pool size :"
  override val tournamentsCreateDirectElimination = "Direct elim. :"
  override val tournamentsCreateActionCancel = "Cancel".uppercase()
  override val tournamentsCreateActionCreate = "Create".uppercase()

  override val tournamentsCreateQualifierSize0 = "No qualifiers"
  override val tournamentsCreateQualifierSizeN = { size: Int -> size.toString() }
  override val tournamentsCreateElimDemomN = { denominator: Int -> "1 / $denominator" }
  override val tournamentsCreateElimDepthFinal = "Final"

  override val tournamentsFilterTitle = "Show only".uppercase()
  override val tournamentsFilterOnlyDone = "Not done".uppercase()
  override val tournamentsFilterOnlyParticipating = "Participating".uppercase()
  override val tournamentsFilterOnlyAdministrating = "Administrating".uppercase()
  override val tournamentsFilterBackContentDescription = "Back"
}

/**
 * Converts a [Duration] to an English string by rounding it to the closest unit of time (seconds
 * minimum).
 */
private fun Duration.toEnglishString(): String {
  if (this >= 1.days) {
    return "${this.inWholeDays} days"
  } else if (this >= 1.hours) {
    return "${this.inWholeHours} hours"
  } else if (this >= 1.minutes) {
    return "${this.inWholeMinutes} minutes"
  }

  return "${this.inWholeSeconds} seconds"
}
