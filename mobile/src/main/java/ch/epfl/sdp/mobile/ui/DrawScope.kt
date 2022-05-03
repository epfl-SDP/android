package ch.epfl.sdp.mobile.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.style.TextDecoration

/**
 * Draws a paragraph on the current [DrawScope].
 *
 * @param paragraph the [Paragraph] that will be drawn.
 * @param color the [Color] of the paragraph.
 * @param topLeft the top left corner at which the paragraph is drawn.
 * @param shadow the [Shadow] to apply to the paragraph.
 * @param textDecoration the [TextDecoration] to apply to the paragraph.
 */
fun DrawScope.drawParagraph(
    paragraph: Paragraph,
    color: Color = Color.Unspecified,
    topLeft: Offset = Offset.Zero,
    shadow: Shadow? = null,
    textDecoration: TextDecoration? = null,
) {
  withTransform(transformBlock = { translate(topLeft.x, topLeft.y) }) {
    paragraph.paint(
        canvas = drawContext.canvas,
        color = color,
        shadow = shadow,
        textDecoration = textDecoration,
    )
  }
}
