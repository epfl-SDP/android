package ch.epfl.sdp.mobile.application.social

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationUser
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * An interface which represents all the endpoints and available features for social interaction for
 * a user of the Pawnies application.
 *
 * @property auth the [Auth] instance which will be used to handle authentication.
 * @property store the [Store] which is used to manage documents.
 */
class SocialFacade(private val auth: Auth, private val store: Store) {

  companion object {

    /** The maximum number of search results that may be displayed. */
    const val MaxSearchResultCount = 10L
  }

  /**
   * Returns the upper search bound when matching prefixes in the search. Essentially, we're
   * interested in the next string, like if we had added "1" to the last character of the string
   * with carry to the characters to its left.
   *
   * There are a few different cases :
   *
   * - If the string is empty, we can't "add 1" to the rightmost character.
   * - If the string already has Char.MAX_VALUE for all its characters, we can't add 1 without
   * changing its length.
   * - Otherwise, we'll keep all the characters up to the last without the value [Char.MAX_VALUE],
   * then add one to the character, and add as many characters with [Char.MIN_VALUE] as needed to
   * the right to form a string of the original length.
   */
  private fun String.upperSearchBound(): String =
      if (isEmpty() || all { it == Char.MAX_VALUE }) this
      else {
        // Replace all the characters after the last character which may be "increased" with the
        // minimum char value, and increment the boundary character.
        val last = indexOfLast { it != Char.MAX_VALUE }
        substring(0, last) + (this[last] + 1) + CharArray(length - last - 1).concatToString()
      }

  /**
   * Searches user by exact match on name.
   *
   * @param text search criteria.
   * @param user the [AuthenticationUser] that is performing the search.
   */
  fun search(
      text: String,
      user: AuthenticationUser = NotAuthenticatedUser,
  ): Flow<List<Profile>> =
      store
          .collection(ProfileDocument.Collection)
          .orderBy(ProfileDocument.Name)
          .whereGreaterThan(ProfileDocument.Name, text, inclusive = true)
          .whereLessThan(ProfileDocument.Name, text.upperSearchBound(), inclusive = true)
          .limit(MaxSearchResultCount)
          .asFlow<ProfileDocument>()
          .map { it.mapNotNull { doc -> doc?.toProfile(user) } }
          .map { list -> list.filterNot { it.uid == (user as? AuthenticatedUser)?.uid } }

  /**
   * Returns a [Flow] of the [Profile] corresponding to a given unique identifier.
   *
   * @param uid the unique identifiers of the profile.
   * @param user the [AuthenticationUser] that is performing the get.
   */
  fun profile(
      uid: String,
      user: AuthenticationUser = NotAuthenticatedUser,
  ): Flow<Profile?> {
    return store
        .collection(ProfileDocument.Collection)
        .document(uid)
        .asFlow<ProfileDocument>()
        .map { doc -> doc?.toProfile(user) }
  }
}
