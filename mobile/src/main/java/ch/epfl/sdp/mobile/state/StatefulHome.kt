package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.home.HomeScaffold
import ch.epfl.sdp.mobile.ui.home.HomeSection

/** The route associated to the social tab. */
private const val SocialRoute = "social"

/** The route associated to the settings tab. */
private const val SettingsRoute = "settings"

/** The route associated to the name editing button on the setting screen. */
private const val SettingEditProfileNameRoute = "profile-name/edit"

/** The route associated to the profile image editing button on the setting screen. */
private const val SettingEditProfileImageRoute = "profile-image/edit"

/** The route associated to the play tab. */
private const val ProfileRoute = "profile"

/** The route associated to the play tab. */
private const val PlayRoute = "play"

/** The route associated to new game screen */
private const val GameRoute = "match"

/** The default identifier for a game. */
private const val GameDefaultId = ""

/** The route associated to new game button in play screen */
private const val PrepareGameRoute = "prepare_game"

/**
 * The route associated to the ar tab. Note : This tab is temporary, use only for the development
 * TODO : Remove this when we can display the entire game on AR
 */
private const val ArRoute = "ar"

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

  val onPersonItemClick: (person: ProfileAdapter) -> Unit = { person ->
    controller.navigate("$ProfileRoute/${person.uid}")
  }

  val onGameItemClick: (match: ChessMatchAdapter) -> Unit = { match ->
    controller.navigate("$GameRoute/${match.uid}")
  }

  HomeScaffold(
      section = section,
      onSectionChange = { controller.navigate(it.toRoute()) },
      hiddenBar = hideBar(entry?.destination?.route),
      modifier = modifier,
  ) { paddingValues ->
    NavHost(
        navController = controller,
        startDestination = SocialRoute,
    ) {
      composable(SocialRoute) {
        StatefulFollowingScreen(
            user = user,
            onShowProfileClick = onPersonItemClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        )
      }
      composable(SettingsRoute) {
        StatefulSettingsScreen(
            user = user,
            modifier = Modifier.fillMaxSize(),
            onEditProfileNameClick = { controller.navigate(SettingEditProfileNameRoute) },
            onEditProfileImageClickAction = { controller.navigate(SettingEditProfileImageRoute) })
      }
      dialog(SettingEditProfileNameRoute) {
        StatefulEditProfileNameDialog(
            user = user,
            onSave = { controller.popBackStack() },
            onCancel = { controller.popBackStack() })
      }
      dialog(SettingEditProfileNameRoute) {
        StatefulEditProfileNameDialog(
          user = user,
          onSave = { controller.popBackStack() },
          onCancel = { controller.popBackStack() })
      }
      composable("$ProfileRoute/{uid}") { backStackEntry ->
        StatefulProfileScreen(
            backStackEntry.arguments?.getString("uid") ?: "", Modifier.fillMaxSize())
      }
      composable(PlayRoute) {
        StatefulPlayScreen(
            user = user,
            onGameItemClick = onGameItemClick,
            navigateToLocalGame = { match -> controller.navigate("$GameRoute/${match.id}") },
            navigateToPrepareGame = { controller.navigate(PrepareGameRoute) },
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues)
      }
      dialog(PrepareGameRoute) {
        StatefulPrepareGameScreen(
            user,
            navigateToGame = { match -> controller.navigate("$GameRoute/${match.id}") },
            cancelClick = { controller.popBackStack() },
        )
      }
      composable("$GameRoute/{id}") { entry ->
        val id = requireNotNull(entry.arguments).getString("id", GameDefaultId)
        val actions =
            StatefulGameScreenActions(
                onBack = { controller.popBackStack() },
                onShowAr = { controller.navigate(ArRoute) },
            )
        StatefulGameScreen(
            actions = actions,
            user = user,
            id = id,
            modifier = Modifier.fillMaxSize(),
            paddingValues = paddingValues,
        )
      }
      composable(ArRoute) { StatefulArScreen(Modifier.fillMaxSize()) }
    }
  }
}

/** Maps a [NavBackStackEntry] to the appropriate [HomeSection]. */
private fun NavBackStackEntry.toSection(): HomeSection =
    when (destination.route) {
      SettingsRoute -> HomeSection.Settings
      ArRoute -> HomeSection.Ar
      PlayRoute -> HomeSection.Play
      else -> HomeSection.Social
    }

/** Maps a [HomeSection] to the appropriate route. */
private fun HomeSection.toRoute(): String =
    when (this) {
      HomeSection.Social -> SocialRoute
      HomeSection.Settings -> SettingsRoute
      HomeSection.Ar -> ArRoute
      HomeSection.Play -> PlayRoute
    }

private fun hideBar(route: String?): Boolean {
  return route?.startsWith(GameRoute) ?: false
}
