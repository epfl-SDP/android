package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch

class FetchedUserProfileScreenState(
    user: Profile,
    matches: List<ChessMatch>,
) : ProfileScreenState {
  override val email = ""
  override val pastGamesCount = 0
  override val matches = matches
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
  val matches =
      remember(chessFacade, profile) { profile?.let { chessFacade.chessMatches(it) } }
          ?.collectAsState(emptyList())
          ?.value
          ?: emptyList()
  val state = profile?.let { FetchedUserProfileScreenState(it, matches) }
  if (state != null) {
    ProfileScreen(state, modifier)
  }
}
