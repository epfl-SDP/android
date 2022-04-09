package ch.epfl.sdp.mobile.ui.game

import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

interface ChessColor

/**
 * The different colors which may be displayed by a [ChessBoard].
 *
 * @param contentDescription the content description for this color.
 */
enum class ClassicColor(
  val contentDescription: LocalizedStrings.() -> String,
) : ChessColor{
  Black(contentDescription = { boardColorBlack }),
  White(contentDescription = { boardColorWhite }),
}
