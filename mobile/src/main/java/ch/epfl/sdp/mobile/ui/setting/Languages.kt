package ch.epfl.sdp.mobile.ui.setting

import ch.epfl.sdp.mobile.state.SWISSGERMAN
import java.util.Locale.*

val Languages =
    mapOf<String, String>(
        Pair("English", ENGLISH.language),
        Pair("Français", FRENCH.language),
        Pair("Deutsch", GERMAN.language),
        Pair("Schiizerdütsch", SWISSGERMAN.language))
