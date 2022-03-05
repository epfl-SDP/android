package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.data.api.AuthenticationApi

// TODO : Implement this.
@Suppress("Unused")
@Composable
fun StatefulProfileScreen(
    user: AuthenticationApi.User,
    modifier: Modifier = Modifier,
) {
  Box(modifier, Alignment.Center) { Text("Settings") }
}
