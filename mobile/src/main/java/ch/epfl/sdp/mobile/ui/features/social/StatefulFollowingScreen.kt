package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.firebase.FirebaseAuthenticatedUser
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO : Implement this.
@Suppress("Unused")
@Composable
fun StatefulFollowingScreen(
        user: AuthenticationApi.User.Authenticated,
        modifier: Modifier = Modifier,
) {
  val localFollowingApi = FirebaseAuthenticatedUser(
          Firebase.auth,
          Firebase.firestore,
          null
  )

  val allUsers by localFollowingApi.following.collectAsState(initial = emptyList())

  Column() {
    allUsers.forEach{ profile ->
      Text(profile.name)
    }
  }
  Box(modifier, Alignment.Center) { Text("Social") }
}
