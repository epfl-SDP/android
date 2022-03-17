package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.home.HomeScaffold
import ch.epfl.sdp.mobile.ui.home.HomeSection

/** The route associated to the social tab. */
private const val SocialRoute = "social"

/** The route associated to the settings tab. */
private const val SettingsRoute = "settings"

/** The route associated to the play tab. */
private const val PlayRoute = "play"

/** The route associated to new game button */
const val GameRoute = "new_game"

/**
 * A stateful composable, which is used at the root of the navigation when the user is
 * authenticated. It displays the bottom navigation sections.
 *
 * @param user the currently logged-in user.
 * @param modifier the [Modifier] for this composable.
 * @param controller the [NavHostController] used to control the current destination.
 */
@Composable
fun StatefulHome(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
    controller: NavHostController = rememberNavController(),
) {
  val entry by controller.currentBackStackEntryAsState()
  val section = entry?.toSection() ?: HomeSection.Social
  HomeScaffold(
      section = section,
      onSectionChange = { controller.navigate(it.toRoute()) },
      hiddenBar = entry?.destination?.route == GameRoute,
      modifier = modifier,
  ) { paddingValues ->
    NavHost(
        navController = controller,
        startDestination = SocialRoute,
    ) {
      composable(SocialRoute) { StatefulFollowingScreen(user, Modifier.fillMaxSize()) }
      composable(SettingsRoute) { StatefulProfileScreen(user, Modifier.fillMaxSize()) }
      composable(PlayRoute) {
        StatefulPlayScreen(controller, Modifier.fillMaxSize(), paddingValues)
      }
      composable(GameRoute) { StatefulGameScreen(user, Modifier.fillMaxSize()) }
    }
  }
}

/** Maps a [NavBackStackEntry] to the appropriate [HomeSection]. */
private fun NavBackStackEntry.toSection(): HomeSection =
    when (destination.route) {
      SettingsRoute -> HomeSection.Settings
      PlayRoute -> HomeSection.Play
      else -> HomeSection.Social
    }

/** Maps a [HomeSection] to the appropriate route. */
private fun HomeSection.toRoute(): String =
    when (this) {
      HomeSection.Social -> SocialRoute
      HomeSection.Settings -> SettingsRoute
      HomeSection.Play -> PlayRoute
    }
