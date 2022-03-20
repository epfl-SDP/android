package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticatedUser,
) : ProfileScreenState {
    override val email = user.email
    override val pastGamesCount = 0
    override val puzzlesCount = 0
    override val matches = emptyList<ChessMatch>()
    override val backgroundColor = Color.Orange
    override val name = user.name
    override val emoji = user.emoji

    override fun onSettingsClick() {}
    override fun onEditClick() {}
}

@Composable
fun StatefulProfileScreen(
    user: AuthenticatedUser,
    profileName: String,
    modifier: Modifier = Modifier,
) {
    val socialFacade = LocalSocialFacade.current
    val scope = rememberCoroutineScope()

    if(user.name == profileName) {
        val state =
            remember(user) { AuthenticatedUserProfileScreenState(user) }
        ProfileScreen(state, modifier)
    } else {
        val state = remember(user) { AuthenticatedUserProfileScreenState(user) }
        ProfileScreen(state, modifier)
    }

    ProfileScreen(state, modifier)
}








