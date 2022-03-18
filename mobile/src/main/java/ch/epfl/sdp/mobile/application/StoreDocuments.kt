package ch.epfl.sdp.mobile.application

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @param name the human-readable name associated to this profile.
 * @param emoji the emoji associated with this profile.
 * @param backgroundColor the hex color code for this profile.
 */
data class ProfileDocument(
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null
)

fun ProfileDocument?.toProfile(): Profile {
  return object : Profile {
    override val emoji: String = this@toProfile?.emoji ?: "😎"
    override val name: String = this@toProfile?.name ?: ""
    override val backgroundColor: Profile.Color =
        this@toProfile?.backgroundColor?.let(Profile::Color) ?: Profile.Color.Default
  }
}
