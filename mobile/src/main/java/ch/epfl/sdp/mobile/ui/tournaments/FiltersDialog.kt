package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors

/** An interface representing the state of the [FiltersDialog] composable. */
@Stable
interface FiltersDialogState {

  /** True if only the done tournaments will be displayed. */
  val onlyShowDone: Boolean

  /** True if only the tournaments in which the user participates will be displayed. */
  val onlyShowParticipating: Boolean

  /** True if only the tournaments administered by the user will be displayed. */
  val onlyShowAdministrating: Boolean

  /** A callback which is called when the "show done" preference is toggled. */
  fun onShowDoneClick()

  /** A callback which is called when the "show participating" preference is toggled. */
  fun onShowParticipatingClick()

  /** A callback which is called when the "show administrating" preference is clicked. */
  fun onShowAdministratingClick()

  /** Called when the user wants to navigate back. */
  fun onBack()
}

/**
 * A dialog which lets the user choose which filters to apply to the list of tournaments.
 *
 * @param state the [FiltersDialogState] which hoists the state of this composable.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun FiltersDialog(
    state: FiltersDialogState,
    modifier: Modifier = Modifier,
) {
  Box(modifier.fillMaxSize(), Alignment.BottomCenter) {

    // Catch clicks on the background to go up.
    Box(
        Modifier.matchParentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = state::onBack,
            ),
    )

    // Bottom sheet.
    Surface(
        elevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
      Column(
          Modifier.padding(vertical = 24.dp).fillMaxWidth(),
          spacedBy(16.dp),
      ) {
        val strings = LocalLocalizedStrings.current
        Text(
            text = strings.tournamentsFilterTitle,
            style = MaterialTheme.typography.button,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Column {
          FilterCheckItem(
              title = strings.tournamentsFilterOnlyDone,
              checked = state.onlyShowDone,
              onClick = state::onShowDoneClick,
              modifier = Modifier.fillMaxWidth(),
          )
          FilterCheckItem(
              title = strings.tournamentsFilterOnlyParticipating,
              checked = state.onlyShowParticipating,
              onClick = state::onShowParticipatingClick,
              modifier = Modifier.fillMaxWidth(),
          )
          FilterCheckItem(
              title = strings.tournamentsFilterOnlyAdministrating,
              checked = state.onlyShowAdministrating,
              onClick = state::onShowAdministratingClick,
              modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

/**
 * An item in the list of available filters.
 *
 * @param title the title of the item.
 * @param checked true iff the item is currently checked.
 * @param onClick a callback which is called whenever the item is pressed.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun FilterCheckItem(
    title: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.clip(RoundedCornerShape(4.dp)).clickable { onClick() }.padding(16.dp),
      horizontalArrangement = spacedBy(16.dp),
      verticalAlignment = CenterVertically,
  ) {
    Checkbox(
        checked = checked,
        onCheckedChange = null,
        colors =
            CheckboxDefaults.colors(
                checkedColor = PawniesColors.Green500,
                uncheckedColor = PawniesColors.Green200,
            ),
    )
    Text(text = title, style = MaterialTheme.typography.button)
  }
}
