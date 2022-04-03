package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticatedUser,
    matches: List<ChessMatch>
) : SettingScreenState {
  override val email = user.email
  override val pastGamesCount = matches.size
  override val puzzlesCount = 0
  override val matches = matches
  override val backgroundColor = Color.Orange
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed

  override fun onSettingsClick() {}
  override fun onEditClick() {}
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val chessFacade = LocalChessFacade.current
  val matches =
      remember(chessFacade, user) { chessFacade.chessMatches(user) }
          .collectAsState(emptyList())
          .value
  val state = remember(user) { AuthenticatedUserProfileScreenState(user, matches) }
  SettingsScreen(state, modifier)
}
