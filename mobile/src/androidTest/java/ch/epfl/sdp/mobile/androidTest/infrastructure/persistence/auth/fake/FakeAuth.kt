package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.fake

import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.AuthBuilder
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth.AuthenticationResult.*
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import java.util.*
import kotlinx.coroutines.flow.*

/**
 * A fake implementation of [Auth] which keeps all the users in memory, alongside with a record of
 * all the user accounts present within it.
 */
class FakeAuth : Auth, AuthBuilder {

  private val record = MutableStateFlow(FakeAuthRecord(currentUser = null, users = emptySet()))

  override val currentUser: Flow<User?>
    get() = record.map { it.currentUser }.distinctUntilChanged()

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ): Auth.AuthenticationResult {
    val users = record.value.users
    val user = users.firstOrNull { it.email == email } ?: return Failure.InvalidUser
    if (user.password != password) {
      return Failure.IncorrectPassword
    }
    record.update { it.copy(currentUser = user) }
    return Success(user)
  }

  override suspend fun signUpWithEmail(
      email: String,
      password: String,
  ): Auth.AuthenticationResult {
    return addUser(email, password)
  }

  override suspend fun signOut() {
    record.update { it.copy(currentUser = null) }
  }

  override fun user(email: String, password: String) {
    addUser(email, password)
  }

  override fun user(email: String, password: String, uid: String) {
    addUser(email, password, uid)
  }

  /**
   * A helper method which automatically adds a new user, if possible, to the [FakeAuth].
   *
   * @param email the email address to use.
   * @param password the password of the added user.
   * @param uid the unique identifier for this newly added user.
   *
   * @return the result of adding the new user.
   */
  private fun addUser(
      email: String,
      password: String,
      uid: String = UUID.randomUUID().toString(),
  ): Auth.AuthenticationResult {

    // Compare-and-set to handle concurrent updates.
    while (true) {
      val rec = record.value
      val users = rec.users

      // If an account already exists with this email or this uid, we must return an authentication
      // failure.
      val exists = users.any { it.email == email || it.uid == uid }
      if (!email.contains('@')) return Failure.IncorrectEmailFormat
      if (password.length < 6) return Failure.BadPassword
      if (exists) return Failure.ExistingAccount

      val newUser = FakeUser(uid = uid, email = email, password = password)
      val newRec = FakeAuthRecord(currentUser = newUser, users = users + newUser)

      // Only update if the data is consistent with what we had read. Otherwise, we can safely retry
      // to perform the whole block atomically. A new UID may be generated, and we'll see newly
      // added users too.
      if (record.compareAndSet(rec, newRec)) return Success(newUser)
    }
  }
}
