package ch.epfl.sdp.mobile.application

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @param name the human-readable name associated to this profile.
 * @param emoji the emoji associated with this profile.
 * @param backgroundColor the hex color code for this profile.
 * @param uid the unique identifier for this profile.
 * @param following a list of unique identifiers of the profiles this profile is following.
 */
data class ProfileDocument(
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null,
    val uid: String? = null,
    val followers: List<String>? = null
)
