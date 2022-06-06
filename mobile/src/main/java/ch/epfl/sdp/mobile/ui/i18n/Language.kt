package ch.epfl.sdp.mobile.ui.i18n

import androidx.compose.ui.Modifier
import java.util.*

/** Language choices for the application. */
enum class Language {
  English,
  German,
  French,
  SwissGerman
}

/**
 * Converts Language to a readable string.
 *
 * @return modifier the [Modifier] for this composable.
 */
fun Language.toReadableString(): String = Languages[this] ?: "English"

/**
 * Converts an ISO String to a Language Enum.
 * @param value which will be transformed into Language.
 *
 * @return an [Language] instance.
 */
fun fromISOStringToLanguage(value: String?): Language =
    when (value) {
      "fr" -> Language.French
      "de" -> Language.German
      "de-ch" -> Language.SwissGerman
      else -> Language.English
    }

/**
 * Converts an Enum to an ISO string.
 * @param value is [Language] which will be converted into a string.
 *
 * @return a [String] format of ISO.
 */
fun fromLanguageToISOString(value: Language): String =
    when (value) {
      Language.French -> "fr"
      Language.German -> "de"
      Language.SwissGerman -> "de-ch"
      else -> "en"
    }

/** A map of Enum values and String representations. */
val Languages =
    mapOf(
        Pair(Language.English, "English"),
        Pair(Language.French, "Français"),
        Pair(Language.German, "Deutsch"),
        Pair(Language.SwissGerman, "Schwiizerdütsch"),
    )
