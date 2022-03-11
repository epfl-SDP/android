package ch.epfl.sdp.mobile.ui.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * A variation of [Button] that displays a loading indicator.
 *
 * @param loading Controls whether the loading indicator should be animated.
 * @param onClick Will be called when the user clicks the button
 * @param modifier Modifier to be applied to the button
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this Button. You can create and pass in your own remembered [MutableInteractionSource] if you
 * want to observe [Interaction]s and customize the appearance / behavior of this Button in
 * different [Interaction]s.
 * @param elevation [ButtonElevation] used to resolve the elevation for this button in different
 * states. This controls the size of the shadow below the button. Pass `null` here to disable
 * elevation for this button. See [ButtonDefaults.elevation].
 * @param shape Defines the button's shape as well as its shadow
 * @param border Border to draw around the button
 * @param colors [ButtonColors] that will be used to resolve the background and content color for
 * this button in different states. See [ButtonDefaults.buttonColors].
 * @param contentPadding The spacing values to apply internally between the container and the
 * content
 * @param content the contents that will be displayed within this button.
 *
 * @see Button The backing implementation of a Material Design button
 */
@Composable
fun LoadingButton(
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
  Button(
      onClick = onClick,
      modifier = modifier,
      enabled = enabled,
      interactionSource = interactionSource,
      elevation = elevation,
      shape = shape,
      border = border,
      colors = colors,
      contentPadding = contentPadding,
  ) {
    content()
    AnimatedVisibility(loading) {
      CircularProgressIndicator(
          modifier = Modifier.padding(start = 16.dp).size(16.dp),
          color = LocalContentColor.current,
          strokeWidth = 2.dp,
      )
    }
  }
}
