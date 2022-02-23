package ch.epfl.sdp.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * An example composable, which displays the number of times a button was pressed. If the button is
 * pressed at least 2 times, an additional text will be displayed.
 *
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun Example(
    modifier: Modifier = Modifier,
) {
  var count by remember { mutableStateOf(0) }
  Box(modifier.fillMaxSize(), Alignment.Center) {
    Button(onClick = { count++ }) {
      val content = "Pressed $count times"
      val style = MaterialTheme.typography.h4

      Text(text = content, style = style)

      if (count >= 2) {
        Text(text = "Yay", style = style)
      }
    }
  }
}
