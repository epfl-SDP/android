package ch.epfl.sdp.mobile.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Adds two [PaddingValues] together, considering the layout direction.
 *
 * @receiver some [PaddingValues] to be added.
 * @param other the second [PaddingValues] which are added.
 * @return the sum of the [PaddingValues].
 */
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    object : PaddingValues {

      override fun calculateBottomPadding() =
          this@plus.calculateBottomPadding() + other.calculateBottomPadding()

      override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
          this@plus.calculateLeftPadding(layoutDirection) +
              other.calculateLeftPadding(layoutDirection)

      override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
          this@plus.calculateRightPadding(layoutDirection) +
              other.calculateRightPadding(layoutDirection)

      override fun calculateTopPadding(): Dp =
          this@plus.calculateTopPadding() + other.calculateTopPadding()
    }
