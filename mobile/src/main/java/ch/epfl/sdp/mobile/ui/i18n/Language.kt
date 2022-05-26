package ch.epfl.sdp.mobile.ui.i18n

import java.util.*

enum class Language {
  English,
  German,
  French,
  SwissGerman
}

fun Language.toReadableString(): String = Languages[this] ?: "English"

fun fromISOStringToLanguage(value: String?): Language =
    when (value) {
      "fr" -> Language.French
      "de" -> Language.German
      "de-ch" -> Language.SwissGerman
      else -> Language.English
    }

fun fromLanguageToISOString(value: Language): String =
    when (value) {
      Language.French -> "fr"
      Language.German -> "de"
      Language.SwissGerman -> "de-ch"
      else -> "en"
    }

val Languages =
    mapOf<Language, String>(
        Pair(Language.English, "English"),
        Pair(Language.French, "Français"),
        Pair(Language.German, "Deutsch"),
        Pair(Language.SwissGerman, "Schwiizerdütsch"))
