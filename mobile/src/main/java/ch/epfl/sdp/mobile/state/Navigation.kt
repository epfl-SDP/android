package ch.epfl.sdp.mobile.state

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.AuthenticationFacade.User
import kotlinx.coroutines.flow.map

/**
 * The root of the application, which determines the top-level state of the navigation. More
 * precisely, it takes care of displaying the proper screens if the user is connected or
 * disconnected.
 */
@Composable
fun Navigation() {
  val state by rememberUserState()
  Crossfade(state) {
    when (it) {
      is UserState.Authenticated -> StatefulHome(it.user)
      UserState.NotAuthenticated -> StatefulAuthenticationScreen()
      UserState.Loading -> Unit
    }
  }
}

/** An interface representing the current state of the user. */
private sealed interface UserState {

  /** The current user is still loading. */
  object Loading : UserState

  /** The user is not connected, and the authentication screen should be displayed. */
  object NotAuthenticated : UserState

  /**
   * The user is authenticated, and the root navigation should be displayed.
   *
   * @param user the [User] information.
   */
  data class Authenticated(val user: User.Authenticated) : UserState
}

/** Returns the current [UserState] in the composition. */
@Composable
private fun rememberUserState(): State<UserState> {
  val auth = LocalAuthenticationFacade.current
  return remember(auth) { auth.currentUser.map { it.toUserState() } }
      .collectAsState(UserState.Loading)
}

/**
 * Maps the given [User] to the appropriate [UserState], which will be used at the root of
 * navigation.
 *
 * @receiver the [User] which is mapped.
 */
private fun User.toUserState(): UserState =
    when (this) {
      User.NotAuthenticated -> UserState.NotAuthenticated
      is User.Authenticated -> UserState.Authenticated(this)
    }
