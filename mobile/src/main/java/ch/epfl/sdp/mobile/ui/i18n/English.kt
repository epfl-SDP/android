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
  override val profileMatchTitle = { opponent: String -> "Against $opponent" }
  override val authenticateOr = "or"

  override val profileMatchInfo = { matchResult: String, reason: String, nMoves: Int ->
    "$matchResult by $reason after $nMoves"
  }
  override val profileTieInfo = { nMoves: Int -> "Tie after $nMoves" }

  override val profilePastGames: String = "Past Games"
  override val profilePuzzle: String = "Puzzles"
  override val profileSettings: String = "Settings"

  /** Social Strings */
  override val socialFollowingTitle = "Following"
  override val socialPerformPlay = "Play"

  override val sectionSocial = "Players"
  override val sectionSettings = "Settings"
}
