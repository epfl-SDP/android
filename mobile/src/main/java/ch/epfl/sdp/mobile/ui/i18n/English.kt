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
  override val authenticateOr = "or"

  override val profileMatchTitle = { opponent: String -> "Against $opponent" }
  override val profileWonByCheckmate = { moves: Int -> "Won by checkmate after $moves moves" }
  override val profileWonByForfeit = { moves: Int -> "Won by forfeit after $moves moves" }
  override val profileLostByCheckmate = { moves: Int -> "Lost by checkmate after $moves moves" }
  override val profileLostByForfeit = { moves: Int -> "Lost by forfeit after $moves moves" }
  override val profileTieInfo = { moves: Int -> "Tie after $moves moves" }

  override val profilePastGames = "Past Games".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileSettings = "Settings"

  override val socialFollowingTitle = "Following"
  override val socialPerformPlay = "Play".uppercase()
  override val socialFollow = "Follow".uppercase()
  override val socialSearchBarPlaceHolder = "Search player ..."
  override val socialSearchEmptyTitle = "Search Any player"
  override val socialSearchEmptySubtitle =
      "Find any player using their name, follow them, or invite them to play or see their match history"

  override val sectionSocial = "Players"
  override val sectionSettings = "Settings"
}
