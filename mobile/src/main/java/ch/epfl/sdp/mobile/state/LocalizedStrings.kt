package ch.epfl.sdp.mobile.state

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.ui.i18n.English
import ch.epfl.sdp.mobile.ui.i18n.French
import ch.epfl.sdp.mobile.ui.i18n.German
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import java.util.Locale.FRENCH
import java.util.Locale.GERMAN

/**
 * An [androidx.compose.runtime.ProvidableCompositionLocal] which provides access to the
 * [LocalizedStrings] for this application.
 */
val LocalLocalizedStrings = compositionLocalOf<LocalizedStrings> { English }

/**
 * Provides the appropriate [LocalizedStrings] from the provided [Configuration], and injects it
 * into the given [content].
 *
 * @param configuration the [Configuration] which is used to select the locale.
 * @param content the composable body which will have the updated [LocalLocalizedStrings].
 */
@Composable
fun ProvideLocalizedStrings(
    configuration: Configuration = LocalConfiguration.current,
    settingsFacade: SettingsFacade,
    content: @Composable () -> Unit,
) {

  val language by
      remember(settingsFacade
        .getLanguage()) {
        settingsFacade
          .getLanguage()}
          .collectAsState(initial = ConfigurationCompat.getLocales(configuration)[0].language)


  val strings =
      when (language) {
        FRENCH.language -> French
        GERMAN.language -> German
        else -> English
      }
  CompositionLocalProvider(LocalLocalizedStrings provides strings) { content() }
}
