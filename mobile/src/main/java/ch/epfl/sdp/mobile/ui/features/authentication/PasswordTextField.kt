package ch.epfl.sdp.mobile.ui.features.authentication

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ch.epfl.sdp.mobile.ui.branding.Branded
import ch.epfl.sdp.mobile.ui.branding.PasswordHide
import ch.epfl.sdp.mobile.ui.branding.PasswordShow
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.material.theme.sharedYAxisEnterTransition
import ch.epfl.sdp.mobile.ui.material.theme.sharedYAxisExitTransition

/**
 * A material design text field which will display a password, and a toggle to show or hide the
 * passwords contents.
 *
 * The following arguments have the same behavior as on a standard [TextField].
 *
 * @param value the input text to be shown in the text field
 * @param onValueChange the callback that is triggered when the input service updates the text. An
 * updated text comes as a parameter of the callback
 * @param modifier a [Modifier] for this text field
 * @param enabled controls the enabled state of the [TextField]. When `false`, the text field will
 * be neither editable nor focusable, the input of the text field will not be selectable, visually
 * text field will appear in the disabled UI state
 * @param readOnly controls the editable state of the [TextField]. When `true`, the text field can
 * not be modified, however, a user can focus it and copy text from it. Read-only text fields are
 * usually used to display pre-filled forms that user can not edit
 * @param textStyle the style to be applied to the input text. The default [textStyle] uses the
 * [LocalTextStyle] defined by the theme
 * @param label the optional label to be displayed inside the text field container. The default text
 * style for internal [Text] is [Typography.caption] when the text field is in focus and
 * [Typography.subtitle1] when the text field is not in focus
 * @param placeholder the optional placeholder to be displayed when the text field is in focus and
 * the input text is empty. The default text style for internal [Text] is [Typography.subtitle1]
 * @param isError indicates if the text field's current value is in error. If set to true, the
 * label, bottom indicator and trailing icon by default will be displayed in error color
 * @param keyboardOptions software keyboard options that contains configuration such as
 * [KeyboardType] and [ImeAction].
 * @param keyboardActions when the input service emits an IME action, the corresponding callback is
 * called. Note that this IME action may be different from what you specified in
 * [KeyboardOptions.imeAction].
 * @param singleLine when set to true, this text field becomes a single horizontally scrolling text
 * field instead of wrapping onto multiple lines. The keyboard will be informed to not show the
 * return key as the [ImeAction]. Note that [maxLines] parameter will be ignored as the maxLines
 * attribute will be automatically set to 1.
 * @param maxLines the maximum height in terms of maximum number of visible lines. Should be equal
 * or greater than 1. Note that this parameter will be ignored and instead maxLines will be set to 1
 * if [singleLine] is set to true.
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this TextField. You can create and pass in your own remembered [MutableInteractionSource] if
 * you want to observe [Interaction]s and customize the appearance / behavior of this TextField in
 * different [Interaction]s.
 * @param shape the shape of the text field's container
 * @param colors [TextFieldColors] that will be used to resolve color of the text, content
 * (including label, placeholder, leading and trailing icons, indicator line) and background for
 * this text field in different states. See [TextFieldDefaults.textFieldColors]
 *
 * @see TextField the reference, underlying text field.
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(
            bottomEnd = ZeroCornerSize,
            bottomStart = ZeroCornerSize,
        ),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
) {
  var hidden by remember { mutableStateOf(true) }
  val transformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None

  TextField(
      value = value,
      onValueChange = onValueChange,
      trailingIcon = {
        VisibilityToggle(
            hidden = hidden,
            onClick = { hidden = !hidden },
        )
      },
      modifier = modifier,
      enabled = enabled,
      readOnly = readOnly,
      textStyle = textStyle,
      label = label,
      placeholder = placeholder,
      isError = isError,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      singleLine = singleLine,
      maxLines = maxLines,
      interactionSource = interactionSource,
      shape = shape,
      colors = colors,
      visualTransformation = transformation,
  )
}

/**
 * A clickable icon which lets the user choose to show or hide the content of a text field.
 *
 * @param hidden true if the content is currently hidden.
 * @param onClick the callback called whenever the icon is pressed.
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun VisibilityToggle(
    hidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val enter = sharedYAxisEnterTransition()
  val exit = sharedYAxisExitTransition()
  AnimatedContent(
      targetState = hidden,
      modifier = modifier,
      transitionSpec = { enter with exit },
  ) { showPassword ->
    IconButton(onClick = onClick) {
      Icon(
          if (showPassword) Icons.Branded.PasswordHide else Icons.Branded.PasswordShow,
          LocalLocalizedStrings.current.authenticatePasswordToggleVisibility,
      )
    }
  }
}
