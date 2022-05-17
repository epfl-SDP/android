package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

import kotlinx.coroutines.flow.Flow

/**
 * An interface representing a facility which may store some values of a type [T].
 *
 * @param T the values which are stored.
 */
interface DataStore<T> {

  /** Returns a [Flow] of the value in this [DataStore]. */
  val data: Flow<T>

  /**
   * Atomically updates the data from the [DataStore] in a serializable fashion.
   *
   * @param transform the function which updates the value atomically.
   * @return the updated value.
   */
  suspend fun updateData(transform: suspend (T) -> T): T
}
