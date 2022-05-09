package ch.epfl.sdp.mobile.state

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.with
import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationUser
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.ui.FadeEnterTransition
import ch.epfl.sdp.mobile.ui.FadeExitTransition
import kotlinx.coroutines.flow.map

/**
 * The root of the application, which determines the top-level state of the navigation. More
 * precisely, it takes care of displaying the proper screens if the user is connected or
 * disconnected.
 */
@Composable
fun Navigation() {
  val state by rememberUserState()
  val transition = updateTransition(state, null)
  transition.AnimatedContent(
      contentKey = { it.key },
      transitionSpec = { FadeEnterTransition with FadeExitTransition },
  ) { current ->
    when (current) {
      is UserState.Authenticated -> StatefulHome(current.user)
      UserState.NotAuthenticated -> StatefulAuthenticationScreen()
      UserState.Loading -> Unit
    }
  }
}

/** An interface representing the current state of the user. */
private sealed interface UserState {

  /**
   * A unique key for this [UserState], which indicates that a full-screen transition should occur.
   */
  val key: Any

  /** The current user is still loading. */
  object Loading : UserState {
    override val key = "Loading"
  }

  /** The user is not connected, and the authentication screen should be displayed. */
  object NotAuthenticated : UserState {
    override val key = "NotAuthenticated"
  }

  /**
   * The user is authenticated, and the root navigation should be displayed.
   *
   * @param user the [AuthenticationUser] information.
   */
  data class Authenticated(val user: AuthenticatedUser<*, *>) : UserState {
    override val key = "Authenticated"
  }
}

/** Returns the current [UserState] in the composition. */
@Composable
private fun rememberUserState(): State<UserState> {
  val auth = LocalAuthenticationFacade.current
  return remember(auth) { auth.currentUser.map { it.toUserState() } }
      .collectAsState(UserState.Loading)
}

/**
 * Maps the given [AuthenticationUser] to the appropriate [UserState], which will be used at the
 * root of navigation.
 *
 * @receiver the [AuthenticationUser] which is mapped.
 */
private fun AuthenticationUser.toUserState(): UserState =
    when (this) {
      NotAuthenticatedUser -> UserState.NotAuthenticated
      is AuthenticatedUser<*, *> -> UserState.Authenticated(this)
    }
