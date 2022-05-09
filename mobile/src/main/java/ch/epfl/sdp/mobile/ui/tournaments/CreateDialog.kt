package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesColors.Beige050
import ch.epfl.sdp.mobile.ui.PawniesColors.Green100
import ch.epfl.sdp.mobile.ui.PawniesColors.Green200
import ch.epfl.sdp.mobile.ui.prepare_game.Dialog

/** An interface which can be used to hoist the state of a [CreateDialog]. */
@Stable
interface CreateDialogState {

  /** The name of the newly created tournament. */
  var name: String

  /** The maximum number of players for the tournament. */
  var maximumPlayerCount: String

  /** True iff the confirm action is enabled. */
  val confirmEnabled: Boolean

  /** A callback which will be called when the confirm action is pressed. */
  fun onConfirm()

  /** A callback which will be called when the dismiss action is pressed. */
  fun onCancel()
}

@Composable
fun CreateDialog(
    state: CreateDialogState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  Dialog(
      modifier = modifier,
      onCancelClick = state::onCancel,
      onConfirmClick = state::onConfirm,
      cancelContent = { Text(strings.tournamentsCreateActionCancel) },
      confirmContent = { Text(strings.tournamentsCreateActionCreate) },
      confirmEnabled = state.confirmEnabled,
      shape = RoundedCornerShape(16.dp),
      content = {
        Column(Modifier.padding(vertical = 16.dp), spacedBy(12.dp)) {
          Text(
              text = strings.tournamentsCreateTitle,
              style = MaterialTheme.typography.subtitle1,
              modifier = Modifier.padding(horizontal = 16.dp),
          )
          OutlinedTextField(
              value = state.name,
              onValueChange = { state.name = it },
              placeholder = { Text(strings.tournamentsCreateNameHint) },
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
              maxLines = 1,
          )
          DashedDivider(
              modifier = Modifier.padding(horizontal = 16.dp),
          )
          Text(
              text = strings.tournamentsCreateRules,
              style = MaterialTheme.typography.subtitle1,
              modifier = Modifier.padding(horizontal = 16.dp),
          )
          Row(Modifier.padding(horizontal = 16.dp), spacedBy(8.dp), CenterVertically) {
            Text(
                text = strings.tournamentsCreateBestOf,
                style = MaterialTheme.typography.subtitle1,
                color = PawniesColors.Green500,
                modifier = Modifier.weight(1f, fill = true),
            )
            // TODO : Load this from state.
            DialogPill(selected = false, onClick = {}) { Text(1.toString()) }
            DialogPill(selected = true, onClick = {}) { Text(3.toString()) }
            DialogPill(selected = false, onClick = {}) { Text(5.toString()) }
          }
          DashedDivider(
              modifier = Modifier.padding(horizontal = 16.dp),
          )
          Text(
              text = strings.tournamentsCreatePlayers,
              style = MaterialTheme.typography.subtitle1,
              modifier = Modifier.padding(horizontal = 16.dp),
          )
          OutlinedTextField(
              value = state.maximumPlayerCount,
              onValueChange = { state.maximumPlayerCount = it },
              placeholder = { Text(strings.tournamentsCreateMaximumPlayerHint) },
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
              maxLines = 1,
              keyboardOptions = KeyboardOptions(keyboardType = Number),
          )
          DialogChoices(
              title = strings.tournamentsCreatePoolSize,
              selected = "No qualifiers",
              items = listOf("No qualifiers", "2", "3"),
              onItemClick = {},
              contentPadding = PaddingValues(horizontal = 16.dp),
          ) { Text(it) }
          DialogChoices(
              title = strings.tournamentsCreateDirectElimination,
              selected = "1 / 4",
              items = listOf("1 / 4", "1 / 2", "Finals"),
              onItemClick = {},
              contentPadding = PaddingValues(horizontal = 16.dp),
          ) { Text(it) }
        }
      },
  )
}

@Composable
private fun DialogPill(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
  val currentSelected = rememberUpdatedState(selected)
  val colors = remember(currentSelected) { DialogPillColors(currentSelected) }
  OutlinedButton(
      onClick = onClick,
      modifier = modifier.sizeIn(minWidth = 40.dp, minHeight = 40.dp),
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 8.dp),
      border = BorderStroke(2.dp, Green200),
      colors = colors,
  ) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center,
        content = content,
    )
  }
}

/** The unique key for the title of a [DialogChoices] component. */
private const val KeyTitle = "title"

/**
 * A composable which displays a list of available choices, which may be selected depending on the
 * preferences of the user.
 *
 * @param T the type of the items to display.
 * @param title the title for the section.
 * @param selected the currently selected item.
 * @param items the list of all the available items.
 * @param onItemClick a callback which is called when an item is pressed.
 * @param modifier the [Modifier] for this composable.
 * @param key the (optional) key for items of type [T].
 * @param contentPadding the [PaddingValues] for the body of the choices.
 * @param itemContent the body of an item to display.
 */
@Composable
private fun <T> DialogChoices(
    title: String,
    selected: T?,
    items: List<T>,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(),
    itemContent: @Composable (T) -> Unit,
) {
  LazyRow(
      modifier = modifier,
      horizontalArrangement = spacedBy(8.dp),
      verticalAlignment = CenterVertically,
      contentPadding = contentPadding,
  ) {
    item(key = KeyTitle) {
      Text(
          text = title,
          style = MaterialTheme.typography.subtitle1,
          color = PawniesColors.Green500,
          modifier = Modifier.width(116.dp),
          maxLines = 1,
          overflow = Ellipsis,
      )
    }
    items(items = items, key = key) {
      DialogPill(
          selected = it == selected,
          onClick = { onItemClick(it) },
      ) { itemContent(it) }
    }
  }
}

/**
 * Some [ButtonColors] which will be applied to a [DialogPill].
 *
 * @param selected a [State] which keeps track of whether the pill is selected or not.
 */
private class DialogPillColors(selected: State<Boolean>) : ButtonColors {

  /** True iff the pill is currently selected. */
  private val selected by selected

  @Composable
  override fun backgroundColor(
      enabled: Boolean,
  ) = animateColorAsState(if (selected) Green100 else Green100.copy(alpha = 0f))

  @Composable
  override fun contentColor(
      enabled: Boolean,
  ) = animateColorAsState(if (selected) Beige050 else Green200)
}
