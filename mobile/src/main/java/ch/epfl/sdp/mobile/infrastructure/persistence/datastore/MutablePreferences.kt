package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

/** An interface representing some write operations available on some [Preferences]. */
interface MutablePreferences : Preferences {

  /**
   * Sets the associated value of the given [Key].
   *
   * @param T the type of the associated value of the [key].
   * @param key the [Key] whose value is set.
   * @param value the value which is set.
   */
  operator fun <T> set(key: Key<T>, value: T)

  /**
   * Removes the value associated with the given key.
   *
   * @param T the type of the associated value of the [key].
   * @param key the [Key] whose value is removed.
   */
  fun <T> remove(key: Key<T>)
}
