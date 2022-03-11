package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.test.suspendForever
import kotlinx.coroutines.flow.emptyFlow

/**
 * A fake implementation of [AuthenticationFacade] which suspends execution and never returns any
 * values for the different API calls.
 */
object SuspendingAuthenticationFacade : AuthenticationFacade {

  override val currentUser = emptyFlow<AuthenticationFacade.User>()

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ) = suspendForever()

  override suspend fun signUpWithEmail(
      email: String,
      name: String,
      password: String,
  ) = suspendForever()
}
