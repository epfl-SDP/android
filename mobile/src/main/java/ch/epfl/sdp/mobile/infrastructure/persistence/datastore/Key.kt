package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

/**
 * A [Key] represents the key of a value in the preferences. Keys should always be built using a
 * [KeyFactory].
 *
 * @param T the type of the associated value.
 */
interface Key<T>
