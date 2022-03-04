package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.runtime.Stable

/** The view-model for the list of people you're following. */
@Stable
interface FollowingState {

  /** The [List] of players which are followed by the current user. */
  val players: List<Person>
}
