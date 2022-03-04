package ch.epfl.sdp.mobile.data.features.authentication

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.suspendForever
import kotlinx.coroutines.flow.emptyFlow

/**
 * A fake implementation of [AuthenticationApi] which suspends execution and never returns any
 * values for the different API calls.
 */
object SuspendingAuthenticationApi : AuthenticationApi {

  override val currentUser = emptyFlow<AuthenticationApi.User>()

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ) = suspendForever()

  override suspend fun signUpWithEmail(
      email: String,
      password: String,
  ) = suspendForever()
}
