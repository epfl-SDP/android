package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.application.AuthenticationFacade.AuthenticationResult.Success
import ch.epfl.sdp.mobile.application.AuthenticationFacade.User
import ch.epfl.sdp.mobile.application.ProfileColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

/**
 * A fake implementation of [AuthenticationFacade] which is backed by an internal state flow, and
 * whose calls to [signInWithEmail] and [signUpWithEmail] will always succeed.
 */
class AlwaysSucceedingAuthenticationFacade : AuthenticationFacade {

  /** The [MutableStateFlow] backing the currently connected user. */
  private val user = MutableStateFlow<AuthenticatedUser?>(null)

  /**
   * An implementation of an [User.Authenticated] user.
   *
   * @param email the email [String] for the user.
   */
  inner class AuthenticatedUser(override val email: String) : User.Authenticated {
    override val following: Flow<List<AuthenticationFacade.Profile>>
      get() = emptyFlow()

    override suspend fun update(block: User.Authenticated.UpdateScope.() -> Unit): Boolean {
      return true
    }

    override suspend fun signOut() {
      user.value = null
    }

    override val emoji: String
      get() = "🇺🇦"
    override val name: String
      get() = "Anne Onyme"
    override val backgroundColor: ProfileColor
      get() = ProfileColor.Pink
  }

  override val currentUser: Flow<User> = user.map { it ?: User.NotAuthenticated }

  override suspend fun signInWithEmail(
      email: String,
      password: String
  ): AuthenticationFacade.AuthenticationResult {
    user.value = AuthenticatedUser(email)
    return Success
  }

  override suspend fun signUpWithEmail(
      email: String,
      name: String,
      password: String
  ): AuthenticationFacade.AuthenticationResult {
    user.value = AuthenticatedUser(email)
    return Success
  }
}
