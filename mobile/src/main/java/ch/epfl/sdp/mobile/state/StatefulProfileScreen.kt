package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Transforms a given [Match] to a [ChessMatch].
 *
 * @param currentUid the uid of the current user.
 * @param match the given [Match]
 * @param facade the used [ChessFacade]
 */
suspend fun Match.toChessMatch(currentUid: String, match: Match, facade: ChessFacade): ChessMatch? {
  val blackID = match.black.filterNotNull().first().uid
  val whiteID = match.white.filterNotNull().first().uid
  val adversary = if (blackID == currentUid) whiteID else blackID
  val result = facade.determineMatchOutcome(match)
  val game = match.game.first()
  val moveNum = game.serialize().size
  result?.let { ChessMatch(adversary, result, moveNum) }
  return null
}

class FetchedUserProfileScreenState(
    user: Profile,
    facade: ChessFacade,
    private val scope: CoroutineScope,
) : ProfileScreenState {
  val allMatches = scope.launch { user?.let { facade.matches(it) }.filterNotNull().first() }
  override val email = ""
  override val pastGamesCount = 0
  override val matches = emptyList<ChessMatch>() // being changed
  override val backgroundColor = user.backgroundColor
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed

  override fun onUnfollowClick() {}
  override fun onChallengeClick() {}
}
/**
 * A stateful composable to visit the profile page of other players
 *
 * @param uid of the player.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulProfileScreen(
    uid: String,
    modifier: Modifier = Modifier,
) {
  val socialFacade = LocalSocialFacade.current
  val chessFacade = LocalChessFacade.current
  val profile by remember(socialFacade, uid) { socialFacade.profile(uid) }.collectAsState(null)
  val scope = rememberCoroutineScope()
  val state = profile?.let { FetchedUserProfileScreenState(it, chessFacade, scope) }
  if (state != null) {
    ProfileScreen(state, modifier)
  }
}
