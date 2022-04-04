package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A composable which displays a dialog with two actions.
 *
 * @param onCancelClick the callback called when the cancel action is picked.
 * @param onConfirmClick the callback called when the confirm action is picked.
 * @param cancelContent the content of the cancel button.
 * @param confirmContent the content of the confirm button.
 * @param modifier the [Modifier] for this composable.
 * @param shape the [Shape] of the dialog.
 * @param elevation the elevation of the dialog.
 * @param content the body of the dialog.
 */
@Composable
fun Dialog(
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    cancelContent: @Composable RowScope.() -> Unit,
    confirmContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
  Dialog(
      modifier = modifier,
      cancel = { OutlinedButton(onCancelClick, shape = CircleShape) { cancelContent() } },
      confirm = { Button(onConfirmClick, shape = CircleShape) { confirmContent() } },
      shape = shape,
      elevation = elevation,
      content = content,
  )
}

/**
 * A composable which displays a dialog which may have a cancel and a confirm action.
 *
 * @param modifier the [Modifier] for this composable.
 * @param cancel the content for the cancel action.
 * @param confirm the content for the confirm action.
 * @param shape the [Shape] of the dialog.
 * @param elevation the elevation of the dialog.
 * @param content the body of the dialog.
 */
@Composable
fun Dialog(
    modifier: Modifier = Modifier,
    cancel: (@Composable () -> Unit)? = null,
    confirm: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
  Surface(
      modifier = modifier.sizeIn(maxHeight = 560.dp, maxWidth = 560.dp),
      shape = shape,
      elevation = elevation,
  ) {
    DialogLayout {
      Box { content() }
      Divider()
      Row(
          modifier = Modifier.padding(8.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
      ) {
        if (cancel != null) cancel()
        if (confirm != null) confirm()
      }
    }
  }
}

/**
 * A custom [Layout] which places the buttons and the divider of a [Dialog] first.
 *
 * @param modifier the [Modifier] of the composable.
 * @param content the contents of the layout.
 */
@Composable
private fun DialogLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  Layout(
      content = content,
      modifier = modifier,
  ) { measurables, constraints ->
    val contentMeasurable = measurables[0]
    val dividerMeasurable = measurables[1]
    val buttonsMeasurable = measurables[2]

    val dividerPlaceable =
        dividerMeasurable.measure(
            constraints.copy(
                minHeight = 0,
                maxHeight = dividerMeasurable.maxIntrinsicHeight(constraints.minWidth),
            ),
        )
    val buttonsPlaceable = buttonsMeasurable.measure(constraints.copy(minHeight = 0))
    val contentPlaceable =
        contentMeasurable.measure(
            constraints.copy(
                minHeight = 0,
                maxHeight =
                    (constraints.maxHeight - dividerPlaceable.height - buttonsPlaceable.height)
                        .coerceAtLeast(0),
            ),
        )
    layout(
        width = maxOf(dividerPlaceable.width, buttonsPlaceable.width, contentPlaceable.width),
        height = dividerPlaceable.height + buttonsPlaceable.height + contentPlaceable.height,
    ) {
      contentPlaceable.place(0, 0)
      dividerPlaceable.place(0, contentPlaceable.height)
      buttonsPlaceable.place(0, contentPlaceable.height + dividerPlaceable.height)
    }
  }
}
