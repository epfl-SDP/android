package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import ch.epfl.sdp.mobile.androidTest.suspendForever
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * A fake implementation of [Auth] which always suspends execution and never returns any values for
 * the different API calls.
 */
object SuspendingAuth : Auth {

  override val currentUser: Flow<User?> = emptyFlow()

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ): Auth.AuthenticationResult = suspendForever()

  override suspend fun signUpWithEmail(
      email: String,
      password: String,
  ): Auth.AuthenticationResult = suspendForever()

  override suspend fun signOut() = suspendForever()
}
