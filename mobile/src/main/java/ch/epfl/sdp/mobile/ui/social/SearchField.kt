package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  val focused by interactionSource.collectIsFocusedAsState()
  val (requester) = FocusRequester.createRefs()
  val controller = LocalSoftwareKeyboardController.current
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = modifier.width(IntrinsicSize.Min).focusRequester(requester),
      placeholder = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(Icons.Branded.Search, null)
          Text(LocalLocalizedStrings.current.socialSearchBarPlaceHolder)
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
                controller?.hide()
              },
          ) { Icon(PawniesIcons.GameClose, null) }
        }
      },
      interactionSource = interactionSource,
      shape = CircleShape,
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      // TODO : Extract these colors.
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              textColor = Color(0xFF356859),
              backgroundColor = Color(0x66B9E4C9),
              placeholderColor = Color(0xFFA9DBBB),
              focusedBorderColor = Color(0xFF356859),
              unfocusedBorderColor = Color(0x00356859),
          ),
  )
}
