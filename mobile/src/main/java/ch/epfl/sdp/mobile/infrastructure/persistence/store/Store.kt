package ch.epfl.sdp.mobile.infrastructure.persistence.store

/**
 * An interface representing a place where collections are stored. This is the top-level of the
 * database hierarchy.
 */
interface Store<D : DocumentReference<C>, out C : CollectionReference<D>> {

  /** Accesses a collection with the given [String] identifier. */
  fun collection(path: String): C

  /**
   * Runs the give [Transaction] on the [Store], executing all the operations atomically. A
   * [Transaction] must perform all its reads before performing all of its writes.
   *
   * This function may throw an [Exception] if a transaction can't be executed.
   *
   * @param scope the body of the [Transaction] to be executed.
   * @return R the return value of the transaction.
   */
  suspend fun <R> transaction(scope: Transaction<D>.() -> R): R
}
