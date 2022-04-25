package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FetchedUserProfileScreenState(
    user: Profile,
    actions: State<ProfileActions>,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : ProfileScreenState<ChessMatchAdapter> {
  private val actions by actions

  override val email = ""
  override var pastGamesCount by mutableStateOf(0)
    private set
  override var matches by mutableStateOf(emptyList<ChessMatchAdapter>())
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
  override fun onMatchClick(match: ChessMatchAdapter) = actions.onMatchClick(match)
}

/**
 * A class representing the different actions available on the profile and settings screen.
 *
 * @param onMatchClick callback function called when a match is clicked on.
 */
data class ProfileActions(
    val onMatchClick: (ChessMatchAdapter) -> Unit,
)

/**
 * A stateful composable to visit the profile page of other players
 *
 * @param uid of the player.
 * @param onMatchClick callback function called when a match is clicked on.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulProfileScreen(
    uid: String,
    onMatchClick: (ChessMatchAdapter) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val actions = rememberUpdatedState(ProfileActions(onMatchClick = onMatchClick))
  val socialFacade = LocalSocialFacade.current
  val chessFacade = LocalChessFacade.current
  val profile by remember(socialFacade, uid) { socialFacade.profile(uid) }.collectAsState(null)
  val scope = rememberCoroutineScope()
  if (profile != null) {
    val state =
        remember(profile) {
          profile?.let { FetchedUserProfileScreenState(it, actions, chessFacade, scope) }
        }
    if (state != null) {
      ProfileScreen(state, modifier, contentPadding)
    }
  }
}
