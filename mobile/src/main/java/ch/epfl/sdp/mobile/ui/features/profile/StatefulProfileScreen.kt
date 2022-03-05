package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import kotlinx.coroutines.launch

// TODO : Implement this.
@Suppress("Unused")
@Composable
fun StatefulProfileScreen(
    user: AuthenticationApi.User.Authenticated,
    modifier: Modifier = Modifier,
) {
  Box(modifier, Alignment.Center) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = { scope.launch { user.signOut() } },
    ) { Text("Sign out") }
  }
}
