package ch.epfl.sdp.mobile.ui.branding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

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
