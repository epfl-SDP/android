package ch.epfl.sdp.mobile.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.sp
import ch.epfl.sdp.mobile.R

/** The IBM Plex Sans font family. */
private val IBMPlexSans =
    FontFamily(
        Font(R.font.ibm_plex_sans_bold, Bold),
        Font(R.font.ibm_plex_sans_extralight, FontWeight.ExtraLight),
        Font(R.font.ibm_plex_sans_light, FontWeight.Light),
        Font(R.font.ibm_plex_sans_medium, Medium),
        Font(R.font.ibm_plex_sans_regular, Normal),
        Font(R.font.ibm_plex_sans_semibold, SemiBold),
        Font(R.font.ibm_plex_sans_thin, FontWeight.Thin),
    )

/** The Noto Sans font family. */
private val NotoSans =
    FontFamily(
        Font(R.font.noto_sans_bold, Bold),
        Font(R.font.noto_sans_regular, Normal),
    )

/** The [Typography] that will be used in the Pawnies application. */
val PawniesTypography =
    Typography(
        defaultFontFamily = NotoSans,
        h1 = TextStyle(fontFamily = IBMPlexSans, fontSize = 96.sp, fontWeight = Bold),
        h2 = TextStyle(fontFamily = IBMPlexSans, fontSize = 60.sp, fontWeight = Bold),
        h3 = TextStyle(fontFamily = IBMPlexSans, fontSize = 48.sp, fontWeight = Bold),
        h4 = TextStyle(fontFamily = IBMPlexSans, fontSize = 34.sp, fontWeight = Bold),
        h5 = TextStyle(fontFamily = IBMPlexSans, fontSize = 24.sp, fontWeight = Bold),
        h6 = TextStyle(fontFamily = IBMPlexSans, fontSize = 20.sp, fontWeight = Bold),
        subtitle1 = TextStyle(fontFamily = NotoSans, fontSize = 16.sp, fontWeight = SemiBold),
        subtitle2 = TextStyle(fontFamily = NotoSans, fontSize = 14.sp, fontWeight = Bold),
        body1 = TextStyle(fontFamily = NotoSans, fontSize = 16.sp, fontWeight = Normal),
        body2 = TextStyle(fontFamily = NotoSans, fontSize = 14.sp, fontWeight = Medium),
        button = TextStyle(fontFamily = NotoSans, fontSize = 16.sp, fontWeight = Bold),
        caption = TextStyle(fontFamily = NotoSans, fontSize = 12.sp, fontWeight = SemiBold),
        overline = TextStyle(fontFamily = NotoSans, fontSize = 10.sp, fontWeight = Bold),
    )
