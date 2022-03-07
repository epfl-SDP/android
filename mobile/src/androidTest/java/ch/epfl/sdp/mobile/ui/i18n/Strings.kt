package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule

/**
 * Sets the current composable as the content of your test screen, and applies the given localized
 * strings to the tested composition.
 *
 * This function should only be used once.
 *
 * @see ComposeContentTestRule.setContent
 *
 * @param strings the [LocalizedStrings] which should be applied.
 * @param content the body of the composable under test.
 *
 * @return the [LocalizedStrings] which were used when setting the content.
 */
fun ComposeContentTestRule.setContentWithLocalizedStrings(
    strings: LocalizedStrings = English,
    content: @Composable () -> Unit,
): LocalizedStrings {
  setContent { CompositionLocalProvider(LocalLocalizedStrings provides strings) { content() } }
  return strings
}
