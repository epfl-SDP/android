package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FetchedUserProfileScreenState(
    user: Profile,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : ProfileScreenState {
  override val email = ""
  override var pastGamesCount by mutableStateOf(0)
    private set
  override var matches by mutableStateOf(emptyList<ChessMatch>())
    private set

  init {
    scope.launch {
      fetchForUser(user, chessFacade).collect { list ->
        matches = list.map { createChessMatch(it, user) }
        pastGamesCount = matches.size
      }
    }
  }

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
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulProfileScreen(
    uid: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val socialFacade = LocalSocialFacade.current
  val chessFacade = LocalChessFacade.current
  val profile by remember(socialFacade, uid) { socialFacade.profile(uid) }.collectAsState(null)
  val scope = rememberCoroutineScope()
  if (profile != null) {
    val state =
        remember(profile) { profile?.let { FetchedUserProfileScreenState(it, chessFacade, scope) } }
    if (state != null) {
      ProfileScreen(state, modifier, contentPadding)
    }
  }
}
