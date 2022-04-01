package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
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
    shape: Shape = RoundedCornerShape(8.dp),
    elevation: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
  Surface(
      modifier = modifier,
      shape = shape,
      elevation = elevation,
  ) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        verticalArrangement = KeepDividerAndButtonsVisible,
    ) {
      Box { content() }
      Divider()
      Row(
          modifier = Modifier.padding(8.dp).align(Alignment.End),
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
      ) {
        OutlinedButton(onCancelClick) { cancelContent() }
        Button(onConfirmClick) { confirmContent() }
      }
    }
  }
}

/** A custom [Arrangement] which keeps the last two elements visible. */
private object KeepDividerAndButtonsVisible : Arrangement.Vertical {

  override fun Density.arrange(
      totalSize: Int,
      sizes: IntArray,
      outPositions: IntArray,
  ) {
    val contentSize = sizes[0]
    val dividerSize = sizes[1]
    val buttonsSize = sizes[2]
    if (contentSize + dividerSize + buttonsSize <= totalSize) {
      outPositions[0] = 0
      outPositions[1] = contentSize
      outPositions[2] = contentSize + dividerSize
    } else {
      outPositions[0] = 0
      outPositions[1] = totalSize - buttonsSize - dividerSize
      outPositions[2] = totalSize - buttonsSize
    }
  }
}
