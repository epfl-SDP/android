package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of the [ProfileScreenState] that performs a given profile's [ChessMatch]
 * requests.
 *
 * @param user the given [Profile].
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class FetchedUserProfileScreenState(
    user: Profile,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : ProfileScreenState, Person by ProfileAdapter(user) {
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
  val emptyProfile: Profile =
      object : Profile {
        override val emoji: String = ""
        override val name: String = ""
        override val backgroundColor: Profile.Color = Profile.Color.Default
        override val uid: String = ""
        override val followed: Boolean = false
      }
  val profile = remember(socialFacade, uid) { socialFacade.profile(uid) }.collectAsState(emptyProfile)
  val scope = rememberCoroutineScope()
  val state = remember(profile) { FetchedUserProfileScreenState(profile, chessFacade, scope) }
  ProfileScreen(state, modifier, contentPadding)
}
