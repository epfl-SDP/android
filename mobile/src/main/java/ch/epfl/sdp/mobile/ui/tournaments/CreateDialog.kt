package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesColors.Beige050
import ch.epfl.sdp.mobile.ui.PawniesColors.Green100
import ch.epfl.sdp.mobile.ui.PawniesColors.Green200
import ch.epfl.sdp.mobile.ui.prepare_game.Dialog

@Stable
interface CreateDialogState {
  var name: String
  var maximumPlayerCount: String
  val confirmEnabled: Boolean
  fun onConfirm()
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
        Column(Modifier.padding(16.dp), spacedBy(12.dp)) {
          Text(strings.tournamentsCreateTitle, style = MaterialTheme.typography.subtitle1)
          OutlinedTextField(
              value = state.name,
              onValueChange = { state.name = it },
              placeholder = { Text(strings.tournamentsCreateNameHint) },
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth(),
              maxLines = 1,
          )
          DashedDivider()
          Text(strings.tournamentsCreateRules, style = MaterialTheme.typography.subtitle1)
          Row(Modifier, spacedBy(8.dp), CenterVertically) {
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
          DashedDivider()
          Text(strings.tournamentsCreatePlayers, style = MaterialTheme.typography.subtitle1)
          OutlinedTextField(
              value = state.maximumPlayerCount,
              onValueChange = { state.maximumPlayerCount = it },
              placeholder = { Text(strings.tournamentsCreateMaximumPlayerHint) },
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth(),
              maxLines = 1,
              keyboardOptions = KeyboardOptions(keyboardType = Number),
          )
          // TODO : Pool size
          // TODO : Direct elim
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
      contentPadding = PaddingValues(),
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

/**
 *
 */
private class DialogPillColors(selected: State<Boolean>) : ButtonColors {

  private val selected by selected

  @Composable
  override fun backgroundColor(
      enabled: Boolean,
  ): State<Color> = animateColorAsState(if (selected) Green100 else Green100.copy(alpha = 0f))

  @Composable
  override fun contentColor(
      enabled: Boolean,
  ): State<Color> = animateColorAsState(if (selected) Beige050 else Green200)
}
