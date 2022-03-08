package ch.epfl.sdp.mobile.ui.i18n

import ch.epfl.sdp.mobile.ui.features.social.MatchResult

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

  /** Profile Strings **/
  override val profileMatchTitle: (String) -> String = {
    opponent: String -> "Against $opponent"
  }

  override val profileMatchInfo: (MatchResult, MatchResult.Reason, Int) -> String =
      { matchResult: MatchResult, reason: MatchResult.Reason, nMoves: Int ->
        when (matchResult) {
          MatchResult.TIE -> "Tie after $nMoves"
          MatchResult.LOSS -> "Won by $reason after $nMoves"
          MatchResult.WIN -> "Lost by $reason after $nMoves"
        }
      }

  override val profilePastGames: String = "Past Games"
  override val profilePuzzle: String = "Puzzles"
  override val profileSettings: String = "Settings"
  
  /** Social Strings  **/
  override val sectionSocial = "Players"
  override val sectionSettings = "Settings"

}
