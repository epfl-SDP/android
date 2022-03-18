package ch.epfl.sdp.mobile.test.ui

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpRect

/**
 * Returns the [DpRect] for the bounds of this [SemanticsNode] in the layout root.
 *
 * @receiver the [SemanticsNode] whose bounds we are computing.
 * @return the [DpRect] for the semantics node bounds.
 *
 * @see [androidx.compose.ui.test.getBoundsInRoot]
 */
fun SemanticsNode.getBoundsInRoot(): DpRect =
    with(root!!.density) {
      boundsInRoot.let { DpRect(it.left.toDp(), it.top.toDp(), it.right.toDp(), it.bottom.toDp()) }
    }

/**
 * Returns true iff the [DpOffset] is contained within this [DpRect] bounds.
 *
 * @receiver the bounds of the [DpRect].
 * @param offset the offset that we're checking some bounds for.
 * @return true if the offset is contained.
 */
operator fun DpRect.contains(offset: DpOffset): Boolean {
  return offset.x in left..right && offset.y in top..bottom
}
