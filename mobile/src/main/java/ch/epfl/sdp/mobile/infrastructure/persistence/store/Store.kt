package ch.epfl.sdp.mobile.infrastructure.persistence.store

/**
 * An interface representing a place where collections are stored. This is the top-level of the
 * database hierarchy.
 */
interface Store {

  /** Accesses a collection with the given [String] identifier. */
  fun collection(path: String): CollectionReference

  /**
   * Runs the give [Transaction] on the [Store], executing all the operations atomically. A
   * [Transaction] must perform all its reads before performing all of its writes.
   *
   * This function may throw an [Exception] if a transaction can't be executed.
   *
   * @param R the type of the return value.
   * @param block the body of the [Transaction] to be executed.
   * @return the return value of the transaction.
   */
  suspend fun <R> transaction(block: Transaction<DocumentReference>.() -> R): R
}
