package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.features.social.Person

/** The view-model of the profile of the currently logged-in user. */
@Stable
interface ProfileState : Person {

  /** The email address of the currently connected user. */
  val email: String
}
