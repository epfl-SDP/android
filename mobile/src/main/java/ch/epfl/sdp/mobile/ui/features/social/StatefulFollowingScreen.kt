package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import ch.epfl.sdp.mobile.data.api.AuthenticationApi

@Preview
@Composable
fun StatefulFollowingScreen(
    user: AuthenticationApi.User.Authenticated,
    modifier: Modifier = Modifier,
) {
  val following by user.following.collectAsState(initial = emptyList())

  Box(modifier, Alignment.Center) { following.forEach { profile ->  Text(profile.name, modifier = Modifier.testTag("following")) } }
}
