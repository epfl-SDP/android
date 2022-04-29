package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/** The view-model of the profile of the screen. */
@Stable
interface ProfileScreenState : Person {

  /** Number of past games */
  val pastGamesCount: Int

  /** List of chess matches */
  val matches: List<ChessMatch>
}
