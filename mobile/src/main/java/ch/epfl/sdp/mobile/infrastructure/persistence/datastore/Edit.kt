package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

/**
 * Edits the [DataStore] with [Preferences], and applies the changes in a serializable fashion.
 *
 * @receiver the [DataStore] on which the updates are performed.
 * @param transform the transformation to apply to the [MutablePreferences].
 * @return the updated [Preferences], after the changes.
 */
suspend fun DataStore<Preferences>.edit(
    transform: suspend (MutablePreferences) -> Unit
): Preferences = updateData { it.toMutablePreferences().apply { transform(this) }.toPreferences() }
