package ch.epfl.sdp.mobile.ui.i18n

/** Localized strings for the English language. */
object English : LocalizedStrings {
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

  override val profilePastGames = "Past Games".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileSettings = "Settings"
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
  override val sectionContests = "Contests"

  override val newGame = "New game".uppercase()
  override val newContest = "New contest".uppercase()

  override val tournamentContestsTitle = "Contests"

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

  override val tournamentsBadgeJoin = "Join"
  override val tournamentsBadgeParticipant = "Participant"
  override val tournamentsBadgeAdmin = "Admin"
}
