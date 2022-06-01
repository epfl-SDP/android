package ch.epfl.sdp.mobile.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * The various [HomeSection] which can be displayed within the application.
 *
 * @param icon the [ImageVector] which acts as the section icon depending on the boolean parameter
 * @param title the title of the section.
 */
enum class HomeSection(
    val icon: @Composable (selected: Boolean) -> Unit,
    val title: LocalizedStrings.() -> String,
) {
  /** The section to play a chess game. */
  Play(
      icon = {
        if (it) Icon(PawniesIcons.SectionPlaySelected, null)
        else Icon(PawniesIcons.SectionPlayUnselected, null)
      },
      title = { sectionPlay },
  ),

  /** The section which displays all the people we follow. */
  Social(
      icon = {
        if (it) Icon(PawniesIcons.SectionSocialSelected, null)
        else Icon(PawniesIcons.SectionSocialUnselected, null)
      },
      title = { sectionSocial },
  ),

  /** The section which displays all the past and current contests. */
  Contests(
      icon = {
        if (it) Icon(PawniesIcons.SectionContestsSelected, null)
        else Icon(PawniesIcons.SectionContestsUnselected, null)
      },
      title = { sectionContests },
  ),

  /** The section to play a chess puzzle game. */
  Puzzles(
      icon = {
        if (it) Icon(PawniesIcons.SectionPuzzlesSelected, null)
        else Icon(PawniesIcons.SectionPuzzlesUnselected, null)
      },
      title = { sectionPuzzles },
  ),

  /** The section to manage our preferences. */
  Settings(
      icon = {
        if (it) Icon(PawniesIcons.SectionSettingsSelected, null)
        else Icon(PawniesIcons.SectionSettings, null)
      },
      title = { sectionSettings },
  ),
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
    hiddenBar: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
      modifier = modifier,
      bottomBar = {
        if (!hiddenBar) {
          BottomNavigation(
              section = section,
              onSectionChange = onSectionChange,
          )
        }
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
  BottomAppBar(
      modifier = modifier,
      backgroundColor = MaterialTheme.colors.background,
  ) {
    for (it in HomeSection.values()) {
      BottomNavigationItem(
          selected = section == it,
          onClick = { onSectionChange(it) },
          icon = { it.icon(section == it) },
          label = {
            Text(
                text = it.title(LocalLocalizedStrings.current),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
          },
          alwaysShowLabel = false,
          selectedContentColor = PawniesColors.Green800,
          unselectedContentColor = PawniesColors.Green200,
      )
    }
  }
}
