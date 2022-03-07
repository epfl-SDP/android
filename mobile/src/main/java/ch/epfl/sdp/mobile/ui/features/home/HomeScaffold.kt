package ch.epfl.sdp.mobile.ui.features.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ch.epfl.sdp.mobile.ui.branding.PawniesIcons
import ch.epfl.sdp.mobile.ui.branding.SectionSettings
import ch.epfl.sdp.mobile.ui.branding.SectionSocial
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * The various [HomeSection] which can be displayed within the application.
 *
 * @param icon the [ImageVector] which acts as the section icon.
 * @param title the title of the section.
 */
enum class HomeSection(
    val icon: ImageVector,
    val title: LocalizedStrings.() -> String,
) {

  /** The section which displays all the people we follow. */
  Social(PawniesIcons.SectionSocial, { sectionSocial }),

  /** The section to manage our preferences. */
  Settings(PawniesIcons.SectionSettings, { sectionSettings }),
}

/**
 * A scaffold which wraps a [HomeSection] composable, and displays a bottom navigation bar which can
 * be used to switch between different destinations within the application.
 *
 * @param section the current [HomeSection].
 * @param onSectionChange the callback called when the [HomeSection] is changed.
 * @param modifier the [Modifier] for this composable.
 * @param content the body of the currently selected section.
 */
@Composable
fun HomeScaffold(
    section: HomeSection,
    onSectionChange: (HomeSection) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
      modifier = modifier,
      bottomBar = {
        BottomNavigation(
            section = section,
            onSectionChange = onSectionChange,
        )
      },
      content = { paddingValues -> content(paddingValues) },
  )
}

/**
 * The bottom navigation bar of the home screen.
 *
 * @param section the currently selected section.
 * @param onSectionChange the callback called when a [HomeSection] is clicked.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun BottomNavigation(
    section: HomeSection,
    onSectionChange: (HomeSection) -> Unit,
    modifier: Modifier = Modifier,
) {
  BottomAppBar(modifier) {
    for (it in HomeSection.values()) {
      BottomNavigationItem(
          selected = section == it,
          onClick = { onSectionChange(it) },
          icon = { Icon(it.icon, null) },
          label = { Text(it.title(LocalLocalizedStrings.current)) },
          alwaysShowLabel = false,
      )
    }
  }
}
