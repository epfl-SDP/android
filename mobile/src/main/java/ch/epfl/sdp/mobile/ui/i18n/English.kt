package ch.epfl.sdp.mobile.ui.i18n

/** Localized strings for the English language. */
object English : LocalizedStrings {
  override val authenticateEmailHint = "Your email"
  override val authenticatePasswordHint = "Password"
  override val authenticatePasswordToggleVisibility = "Toggle password visibility"
  override val authenticatePerformLogIn = "Log in"
  override val authenticatePerformRegister = "Register"
  override val authenticateSwitchToLogIn = "Log in with email"
  override val authenticateSwitchToRegister = "Register with email"

  override val authenticateErrorFailure = "Something went wrong when authenticating"
  override val profileMatchTitle = { opponent: String -> "Against $opponent" }
  override val profileWonByCheckmate = { moves: Int -> "Won by checkmate after $moves moves" }
  override val profileWonByForfeit = { moves: Int -> "Won by forfeit after $moves moves" }
  override val profileLostByCheckmate = { moves: Int -> "Lost by checkmate after $moves moves" }
  override val profileLostByForfeit = { moves: Int -> "Lost by forfeit after $moves moves" }
  override val profileTieInfo = { moves: Int -> "Tie after $moves moves" }

  override val profilePastGames = "Past Games".uppercase()
  override val profilePuzzle = "Puzzles".uppercase()
  override val profileSettings = "Settings"

  override val sectionSocial = "Players"
  override val sectionSettings = "Settings"
}
