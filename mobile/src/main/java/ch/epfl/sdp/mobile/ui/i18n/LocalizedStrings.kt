package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.features.social.MatchResult

/**
 * An interface which defines all the texts and content descriptions which are used in the Pawnies
 * application.
 */
@Stable
interface LocalizedStrings {
  val authenticateEmailHint: String
  val authenticatePasswordHint: String
  val authenticatePasswordToggleVisibility: String
  val authenticatePerformLogIn: String
  val authenticatePerformRegister: String
  val authenticateSwitchToLogIn: String
  val authenticateSwitchToRegister: String
  val authenticateErrorFailure: String
  /** Profile Strings **/
  val profileMatchTitle: (String) -> String
  val profileMatchInfo: (MatchResult, MatchResult.Reason, Int) -> String
  val profilePastGames: String
  val profilePuzzle: String
  val profileSettings: String
}
