package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.PuzzleId
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.profile.VisitedProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * An implementation of the [VisitedProfileScreenState] that performs a given profile's [ChessMatch]
 * requests.
 *
 * @param currentUser current logged in user
 * @param user the given [Profile].
 * @param actions the [ProfileActions] which are available on the screen.
 * @param onChallengeClickAction callback if challenge button clicked
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class FetchedUserProfileScreenState(
    private val currentUser: AuthenticatedUser,
    private val user: Profile,
    actions: State<ProfileActions>,
    onChallengeClickAction: State<(String) -> Unit>,
    chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) :
    VisitedProfileScreenState<ChessMatchAdapter>,
    ProfileScreenState<ChessMatchAdapter> by StatefulProfileScreen(
        user, actions, chessFacade, scope) {

  val onChallengeClickAction by onChallengeClickAction

  override var follows by mutableStateOf(false)

  init {
    scope.launch {
      currentUser.following
          .map { list -> list.map { ProfileAdapter(it) }.any { el -> el.uid == user.uid } }
          .collect { follows = it }
    }
  }

  override fun onChallengeClick() {
    onChallengeClickAction(user.uid)
  }

  override fun onFollowClick() {
    scope.launch {
      if (!follows) {
        currentUser.follow(user)
      } else {
        currentUser.unfollow(user)
      }
    }
  }
}

/**
 * A stateful composable to visit the profile page of other players
 *
 * @param uid of the player.
 * @param onMatchClick callback function called when a match is clicked on.
 * @param onChallengeClick callback if challenge button clicked
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulVisitedProfileScreen(
    user: AuthenticatedUser,
    uid: String,
    onMatchClick: (ChessMatchAdapter) -> Unit,
    onBackToSocialClick: () -> Unit,
    onChallengeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val actions =
      rememberUpdatedState(
          ProfileActions(onMatchClick = onMatchClick, onBack = onBackToSocialClick))
  val socialFacade = LocalSocialFacade.current
  val chessFacade = LocalChessFacade.current
  val onChallengeClick = rememberUpdatedState(onChallengeClick)

  val profile by
      remember(socialFacade, uid) { socialFacade.profile(uid).map { it ?: EmptyProfile } }
          .collectAsState(EmptyProfile)
  val scope = rememberCoroutineScope()
  val state =
      remember(actions, profile, chessFacade, scope, socialFacade) {
        FetchedUserProfileScreenState(user, profile, actions, onChallengeClick, chessFacade, scope)
      }
  ProfileScreen(state, modifier, contentPadding)
}

private object EmptyProfile : Profile {
  override val emoji: String = ""
  override val name: String = ""
  override val backgroundColor: Profile.Color = Profile.Color.Default
  override val uid: String = ""
  override val followed: Boolean = false
  override val solvedPuzzles = emptyList<PuzzleId>()
}
