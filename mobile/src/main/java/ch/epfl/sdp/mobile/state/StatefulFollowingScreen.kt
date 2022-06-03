package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A class that turns a provided [Profile] into a [Person].
 *
 * @property profile the [Profile] to turn into a [Person].
 */
data class ProfileAdapter(
    val profile: Profile,
) : Person {

  /** The unique identifier of the underlying [Profile]. */
  val uid = profile.uid

  override val backgroundColor = profile.backgroundColor.toColor()
  override val name = profile.name
  override val emoji = profile.emoji
  override val followed = profile.followed
}

/**
 * A stateful implementation of the [SocialScreen] composable, which uses some composition-local
 * values to retrieve the appropriate dependencies.
 *
 * @param user the current [AuthenticatedUser].
 * @param onShowProfileClick the callback called when we want to show the profile of a user.
 * @param onPlayClick the callback called when we click on the play button
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun StatefulFollowingScreen(
    user: AuthenticatedUser,
    onShowProfileClick: (ProfileAdapter) -> Unit,
    onPlayClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val actions =
      rememberUpdatedState(
          Actions(onShowProfileClick = onShowProfileClick, onPlayClick = onPlayClick))
  val socialFacade = LocalSocialFacade.current
  val scope = rememberCoroutineScope()

  val state =
      remember(
          user,
          socialFacade,
          scope,
      ) { SnapshotSocialScreenState(actions, user, socialFacade, scope) }

  SocialScreen(
      state = state,
      modifier = modifier,
      key = { it.uid },
      contentPadding = contentPadding,
  )
}

/**
 * An interface representing the different actions available on the profile that we see on the
 * social screen.
 */
interface FollowingProfileActions {

  /** Callback when a user is clicked to display their profile. */
  val onShowProfileClick: (ProfileAdapter) -> Unit

  /** Callback function for a clicked on play. */
  val onPlayClick: (String) -> Unit
}

/** A class representing the different actions available on the social screen. */
data class Actions(
    override val onShowProfileClick: (ProfileAdapter) -> Unit,
    override val onPlayClick: (String) -> Unit
) : FollowingProfileActions

/**
 * An implementation of the [SocialScreenState] that performs social requests.
 *
 * It uses a [ProfileAdapter] as the generic [Person] type to be able to retrieve the [Person]'s uid
 * by converting it to a [Profile]
 *
 * @param actions the [Actions] which are available on the screen.
 * @property user the current [AuthenticatedUser].
 * @property socialFacade the [SocialFacade] used to perform some requests.
 * @property scope the [CoroutineScope] on which requests are performed.
 */
class SnapshotSocialScreenState(
    actions: State<Actions>,
    private val user: AuthenticatedUser,
    private val socialFacade: SocialFacade,
    private val scope: CoroutineScope,
) : SocialScreenState<ProfileAdapter> {

  private val actions by actions

  override val searchFieldInteraction = MutableInteractionSource()

  private var focused by mutableStateOf(false)

  override val mode: SocialScreenState.Mode
    get() = if (focused || input.isNotEmpty()) Searching else Following

  init {
    scope.launch {
      searchFieldInteraction.interactions.reduceIsFocused().onEach { focused = it }.collect()
    }
  }

  override var input by mutableStateOf("")

  override var searchResult by mutableStateOf(emptyList<ProfileAdapter>())
    private set

  init {
    scope.launch {
      snapshotFlow { input }
          .flatMapLatest { s -> socialFacade.search(s, user) }
          .map { list -> list.map { ProfileAdapter(it) } }
          .onEach { searchResult = it }
          .collect()
    }
  }

  override var following by mutableStateOf(emptyList<ProfileAdapter>())
    private set

  init {
    scope.launch {
      user.following
          .map { list -> list.map { ProfileAdapter(it) } }
          .onEach { following = it }
          .collect()
    }
  }

  override fun onShowProfileClick(person: ProfileAdapter) = actions.onShowProfileClick(person)

  override fun onPlayClick(opponent: ProfileAdapter) = actions.onPlayClick(opponent.uid)

  override fun onFollowClick(followed: ProfileAdapter) {
    scope.launch {
      if (!followed.followed) {
        user.follow(followed.profile)
      } else {
        user.unfollow(followed.profile)
      }
    }
  }
}

/**
 * Reduces a [Flow] of [Interaction] to determine whether the flow represents a currently focused
 * element. This is a [Flow]-based equivalent to [MutableInteractionSource]'s
 * `collectIsFocusedAsState()`.
 *
 * @receiver the [Flow] of [Interaction] that is reduced.
 * @return a [Flow] of [Boolean] which emits true iff the state is focused.
 */
private fun Flow<Interaction>.reduceIsFocused(): Flow<Boolean> = flow {
  val focused = mutableListOf<FocusInteraction.Focus>()
  collect { interaction ->
    when (interaction) {
      is FocusInteraction.Focus -> focused.add(interaction)
      is FocusInteraction.Unfocus -> focused.remove(interaction.focus)
    }
    emit(focused.isNotEmpty())
  }
}
