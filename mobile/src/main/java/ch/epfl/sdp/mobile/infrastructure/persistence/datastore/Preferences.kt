package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

/** An interface representing some read-only key-value preferences. */
interface Preferences {

  /**
   * Returns true iff the [Preferences] contain the [key].
   *
   * @param T the type of the associated value of the [key].
   * @param key the [Key] whose presence is checked.
   * @return true iff the key is in the [Preferences].
   */
  operator fun <T> contains(key: Key<T>): Boolean

  /**
   * Returns the associated value of the given [Key].
   *
   * @param T the type of the associated value of the [key].
   * @param key the [Key] whose value is queried.
   * @return the associated value, or null if absent.
   */
  operator fun <T> get(key: Key<T>): T?

  /**
   * Returns a copy of these [Preferences] which may be mutated.
   *
   * @see MutablePreferences
   */
  fun toMutablePreferences(): MutablePreferences
}
