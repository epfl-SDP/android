package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*

/**
 * An [OutlinedTextField] which displays a centered search placeholder, and which may be used as an
 * input field for search queries.
 *
 * @param value the current text value of this field.
 * @param onValueChange called when the input value changes.
 * @param modifier the [Modifier] for this composable.
 * @param interactionSource the [MutableInteractionSource] for this composable.
 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  val strings = LocalLocalizedStrings.current
  val focused by interactionSource.collectIsFocusedAsState()
  val manager = LocalFocusManager.current
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = modifier.width(IntrinsicSize.Min),
      placeholder = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Branded.Search, null)
          Text(strings.socialSearchBarPlaceHolder, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
      },
      trailingIcon = {
        AnimatedVisibility(
            visible = focused,
            enter = FadeEnterTransition,
            exit = FadeExitTransition,
        ) {
          IconButton(
              onClick = {
                // Clear the text and hide the keyboard.
                onValueChange("")
                manager.clearFocus()
              },
          ) {
            Icon(
                imageVector = PawniesIcons.Close,
                contentDescription = strings.socialSearchClearContentDescription,
            )
          }
        }
      },
      interactionSource = interactionSource,
      shape = CircleShape,
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions { manager.clearFocus() },
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              textColor = PawniesColors.Green800,
              backgroundColor = PawniesColors.Green100.copy(alpha = 0.4f),
              placeholderColor = PawniesColors.Green200,
              focusedBorderColor = PawniesColors.Green800,
              unfocusedBorderColor = PawniesColors.Green800.copy(alpha = 0f),
          ),
  )
}
