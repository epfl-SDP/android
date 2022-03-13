package ch.epfl.sdp.mobile.ui.social

import androidx.compose.runtime.Stable

/** The view-model for the list of people that correspond to the query in the search bar. */
@Stable
interface SearchState {

  /** The [List] of players which are returned by the query. */
  val players: List<Person>
}
