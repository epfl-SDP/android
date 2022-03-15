package ch.epfl.sdp.mobile.state

import android.util.Log
import android.util.Log.INFO
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.authentication.AuthenticationResult
import ch.epfl.sdp.mobile.application.social.SearchResult
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.logging.Level.INFO

private data class ProfileAdapter(
    private val profile: Profile,
) : Person {
    override val backgroundColor: Color
        get() = profile.backgroundColor
    override val name: String
        get() = profile.name
    override val emoji: String
        get() = profile.emoji
}

@Composable
fun StatefulFollowingScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
    val following = remember(user) {
            user.following
        }.collectAsState(emptyList()).value.map{ProfileAdapter(it)}

    val socialFacade = LocalSocialFacade.current
    val input =  remember { mutableStateOf("")}  // MutalStateOf or Snapshot flow
    val searchResults = remember {
        snapshotFlow { input }
            .flatMapLatest { s -> socialFacade.search(s.value) }
        }.collectAsState(emptyList())
        .value.map { ProfileAdapter(it)}

    searchResults.forEach{ el -> Log.i("info", el.name)}
    val mode = remember{mutableStateOf(Following)}


    SocialScreen(SnapshotSocialScreenState(following, input, searchResults, mode), modifier.fillMaxSize())
}

private class SnapshotSocialScreenState(
    following: List<Person>,
    input: MutableState<String>,
    searchResult: List<Person>,
    mode: MutableState<SocialScreenState.Mode>,
) : SocialScreenState {
    override var following =  following
    override var input by input
    override var searchResult = searchResult
    override var mode by mode


    override fun onValueChange() {
        if(mode == SocialScreenState.Mode.Searching) {
            mode = SocialScreenState.Mode.Following
        } else {
            mode = SocialScreenState.Mode.Searching
        }
    }
}