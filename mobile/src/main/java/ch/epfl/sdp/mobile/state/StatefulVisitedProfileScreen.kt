package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.profile.VisitedProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map

/**
 * An implementation of the [VisitedProfileScreenState] that performs a given profile's [ChessMatch]
 * requests.
 *
 * @param user the given [Profile].
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class FetchedUserProfileScreenState(
    user: Profile,
    chessFacade: ChessFacade,
    val onBackToSocialClick: () -> Unit,
    scope: CoroutineScope,
) :
    VisitedProfileScreenState,
    ProfileScreenState by StatefulProfileScreen(user, chessFacade, scope) {

  override fun onUnfollowClick() {}
  override fun onChallengeClick() {}
  override fun onBackClick() {
    onBackToSocialClick()
  }
}

/**
 * A stateful composable to visit the profile page of other players
 *
 * @param uid of the player.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] to apply to this screen.
 */
@Composable
fun StatefulVisitedProfileScreen(
    uid: String,
    modifier: Modifier = Modifier,
    onBackToSocialClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
  val socialFacade = LocalSocialFacade.current
  val chessFacade = LocalChessFacade.current
  val profile by
      remember(socialFacade, uid) { socialFacade.profile(uid).map { it ?: EmptyProfile } }
          .collectAsState(EmptyProfile)
  val scope = rememberCoroutineScope()
  val state =
      remember(profile, chessFacade, onBackToSocialClick, scope) {
        FetchedUserProfileScreenState(profile, chessFacade, onBackToSocialClick, scope)
      }
  ProfileScreen(state, modifier, contentPadding)
}

private object EmptyProfile : Profile {
  override val emoji: String = ""
  override val name: String = ""
  override val backgroundColor: Profile.Color = Profile.Color.Default
  override val uid: String = ""
  override val followed: Boolean = false
}
