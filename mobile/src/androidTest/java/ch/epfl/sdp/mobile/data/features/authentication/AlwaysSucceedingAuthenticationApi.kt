package ch.epfl.sdp.mobile.data.features.authentication

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Success
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * A fake implementation of [AuthenticationApi] which is backed by an internal state flow, and whose
 * calls too [signInWithEmail] and [signUpWithEmail] will always succeed.
 */
class AlwaysSucceedingAuthenticationApi : AuthenticationApi {

  /** The [MutableStateFlow] backing the currently connected user. */
  private val user = MutableStateFlow<UserImpl?>(null)

  /**
   * An implementation of an [User.Authenticated] user.
   *
   * @param email the email [String] for the user.
   */
  private inner class UserImpl(override val email: String) : User.Authenticated {

    override suspend fun signOut() {
      user.value = null
    }
  }

  override val currentUser: Flow<User> = user.map { it ?: User.NotAuthenticated }

  override suspend fun signInWithEmail(
      email: String,
      password: String
  ): AuthenticationApi.AuthenticationResult {
    user.value = UserImpl(email)
    return Success
  }

  override suspend fun signUpWithEmail(
      email: String,
      password: String
  ): AuthenticationApi.AuthenticationResult {
    user.value = UserImpl(email)
    return Success
  }
}
