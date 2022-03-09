package ch.epfl.sdp.mobile.data.features.authentication.api.firebase

import ch.epfl.sdp.mobile.backend.store.fake.buildStore
import ch.epfl.sdp.mobile.backend.store.fake.document
import ch.epfl.sdp.mobile.backend.store.fake.emptyStore
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Success
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User.Authenticated
import ch.epfl.sdp.mobile.data.api.ProfileColor
import ch.epfl.sdp.mobile.data.api.firebase.FirebaseAuthenticationApi
import ch.epfl.sdp.mobile.data.api.firebase.FirebaseProfileDocument
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirebaseAuthenticationApiTest {

  @Test
  fun currentUser_startsWithLoading() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.currentUser.first()).isEqualTo(User.Loading)
  }

  @Test
  fun signIn_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = mockk<FirebaseAuth>()
    val result = mockk<AuthResult>()
    val user = mockk<FirebaseUser>()
    val firestore = emptyStore()

    every { result.user } returns user
    every { auth.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signInWithEmail("email", "password")).isEqualTo(Success)
  }

  @Test
  fun signIn_withEmptyAuthResult_returnsFailure() = runTest {
    val auth = mockk<FirebaseAuth>()
    val result = mockk<AuthResult>()
    val firestore = emptyStore()

    every { result.user } returns null
    every { auth.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signInWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun signUp_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = mockk<FirebaseAuth>()
    val result = mockk<AuthResult>()
    val user = mockk<FirebaseUser>()
    val firestore = emptyStore()

    every { result.user } returns user
    every { auth.createUserWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signUpWithEmail("email", "password")).isEqualTo(Success)
  }

  @Test
  fun signUp_withThrowingAuthResult_returnsFailure() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()

    every { auth.createUserWithEmailAndPassword(any(), any()) } returns
        Tasks.forException(Exception())

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signUpWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun signUp_withoutAuthResult_returnsFailure() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()

    every { auth.createUserWithEmailAndPassword(any(), any()) } returns Tasks.forResult(null)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signUpWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun firstNonLoadingUser_isEmittedByListener() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val user = mockk<FirebaseUser>()
    val email = "alexandre.piveteau@epfl.ch"

    every { user.email } returns email
    every { user.uid } returns "uid"
    mockAuthCurrentUser(auth, user)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.currentUser.filterIsInstance<Authenticated>().first().email).isEqualTo(email)
    verify {
      auth.addAuthStateListener(any())
      auth.removeAuthStateListener(any())
    }
  }

  @Test
  fun firstNonLoadingUserWithoutEmail_isEmittedByListener() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val user = mockk<FirebaseUser>()

    every { user.email } returns null
    every { user.uid } returns "uid"
    mockAuthCurrentUser(auth, user)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.currentUser.filterIsInstance<Authenticated>().first().email).isEqualTo("")
    verify {
      auth.addAuthStateListener(any())
      auth.removeAuthStateListener(any())
    }
  }

  @Test
  fun firstNonLoadingUser_canSignOut() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val user = mockk<FirebaseUser>()

    every { user.email } returns null
    every { user.uid } returns "uid"
    mockAuthCurrentUser(auth, user)
    every { auth.signOut() } returns Unit

    val api = FirebaseAuthenticationApi(auth, firestore)

    api.currentUser.filterIsInstance<Authenticated>().first().signOut()
    verify { auth.signOut() }
  }

  @Test
  fun authenticatedUserCanUpdateItsProfileAndCanSeeUpdatedValues() = runTest {
    val auth = mockk<FirebaseAuth>()
    val user = mockk<FirebaseUser>()
    mockAuthCurrentUser(auth, user)

    val firestore = buildStore {
      collection("users") { document("uid", FirebaseProfileDocument()) }
    }
    val api = FirebaseAuthenticationApi(auth, firestore)

    every { user.uid } returns "uid"
    every { user.email } returns "email@email.com"

    val userAuthenticated = api.currentUser.filterIsInstance<Authenticated>().first()

    userAuthenticated.update {
      emoji = "🇺🇦"
      backgroundColor = ProfileColor.Pink
      name = "MyNewName"
    }

    val updatedUser = api.currentUser.filterIsInstance<Authenticated>().first()
    assertThat(updatedUser.name).isEqualTo("MyNewName")
    assertThat(updatedUser.emoji).isEqualTo("🇺🇦")
    assertThat(updatedUser.backgroundColor).isEqualTo(ProfileColor.Pink)
  }

  @Test
  fun authenticatedUserCanGetFollowing() = runTest {
    val auth = mockk<FirebaseAuth>()
    val user = mockk<FirebaseUser>()
    mockAuthCurrentUser(auth, user)

    data class TestProfile(
        override val emoji: String,
        override val name: String,
        override val backgroundColor: ProfileColor,
    ) : AuthenticationApi.Profile

    fun TestProfile.toFirebaseProfileDocument(): FirebaseProfileDocument {
      return FirebaseProfileDocument(
          emoji = this.emoji,
          name = this.name,
          backgroundColor =
              when (this.backgroundColor) {
                ProfileColor.Pink -> "pink"
                else -> "pink"
              })
    }

    val matthieu =
        TestProfile(
            emoji = "👌",
            name = "Matthieu",
            backgroundColor = ProfileColor.Pink,
        )
    val alexandre =
        TestProfile(
            emoji = "⚒",
            name = "Alexandre",
            backgroundColor = ProfileColor.Pink,
        )

    val firestore = buildStore {
      collection("users") {
        document("uid1", matthieu.toFirebaseProfileDocument())
        document("uid2", alexandre.toFirebaseProfileDocument())
      }
    }
    val api = FirebaseAuthenticationApi(auth, firestore)

    every { user.uid } returns "uid"
    every { user.email } returns "email@email.com"

    val userAuthenticated = api.currentUser.filterIsInstance<Authenticated>().first()
    val following = userAuthenticated.following.first()

    assertThat(following.map { profile -> profile.name })
        .containsExactly(matthieu.name, alexandre.name)
  }

  @Test
  fun gettingProfileWithNullValuesAssignsDefaultValues() = runTest {
    val auth = mockk<FirebaseAuth>()
    val user = mockk<FirebaseUser>()
    mockAuthCurrentUser(auth, user)

    val firestore = buildStore {
      collection("users") {
        document(
            "uid",
            FirebaseProfileDocument(
                name = null,
                emoji = null,
                backgroundColor = null,
            ))
      }
    }
    val api = FirebaseAuthenticationApi(auth, firestore)

    every { user.uid } returns "uid"
    every { user.email } returns "email@email.com"

    val userAuthenticated = api.currentUser.filterIsInstance<Authenticated>().first()
    val following = userAuthenticated.following.first()

    val profile = following[0]
    assertThat(profile.name).isEqualTo("")
    assertThat(profile.emoji).isEqualTo("😎")
    assertThat(profile.backgroundColor).isEqualTo(ProfileColor.Pink)
  }

  @Test
  fun gettingFromAuthenticatedUserUpdateScopeThrowsError() = runTest {
    val auth = mockk<FirebaseAuth>()
    val user = mockk<FirebaseUser>()
    mockAuthCurrentUser(auth, user)

    val firestore = emptyStore()
    val api = FirebaseAuthenticationApi(auth, firestore)

    every { user.uid } returns "uid"
    every { user.email } returns "email@email.com"

    val userAuthenticated = api.currentUser.filterIsInstance<Authenticated>().first()

    val canGetName = userAuthenticated.update { this.name }

    val canGetEmoji = userAuthenticated.update { this.emoji }

    val canGetColor = userAuthenticated.update { this.backgroundColor }

    assertThat(canGetName).isEqualTo(false)
    assertThat(canGetEmoji).isEqualTo(false)
    assertThat(canGetColor).isEqualTo(false)
  }

  private fun mockAuthCurrentUser(
      auth: FirebaseAuth,
      user: FirebaseUser,
  ) {
    every { auth.addAuthStateListener(any()) } answers
        { call ->
          val listener = call.invocation.args[0] as FirebaseAuth.AuthStateListener
          every { auth.currentUser } returns user
          listener.onAuthStateChanged(auth)
        }
    every { auth.removeAuthStateListener(any()) } returns Unit
  }
}
