package ch.epfl.sdp.mobile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

/** Returns the [PawniesIcons] from which Pawnies icons are queried. */
val Icons.Branded: PawniesIcons
  get() = PawniesIcons

/** An object that contains all the icons that will be used in Pawnies. */
object PawniesIcons

/** An icon to show a password. */
val PawniesIcons.PasswordShow
  get() = Icons.Default.Visibility

/** An icon to hide a password. */
val PawniesIcons.PasswordHide
  get() = Icons.Default.VisibilityOff

/** An icon for the social section. */
val PawniesIcons.SectionSocial
  get() = Icons.Default.People

/** An icon for the settings section. */
val PawniesIcons.SectionSettings
  get() = Icons.Default.Settings

/** An icon for the game section. */
val PawniesIcons.SectionGame
  get() = Icons.Default.Piano
