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
  override val profileCheckmate = "checkmate"
  override val profileForfeit = "forfeit"
  override val profileWinInfo = { reason: String, nMoves: Int -> "Won by $reason after $nMoves" }
  override val profileLossInfo = { reason: String, nMoves: Int -> "Loss by $reason after $nMoves" }
  override val profileTieInfo = { nMoves: Int -> "Tie after $nMoves" }

  override val profilePastGames = "Past Games"
  override val profilePuzzle = "Puzzles"
  override val profileSettings = "Settings"

  override val sectionSocial = "Players"
  override val sectionSettings = "Settings"
}
