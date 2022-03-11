package ch.epfl.sdp.mobile.infrastructure.persistence.store

/**
 * An interface representing a place where collections are stored. This is the top-level of the
 * database hierarchy.
 */
interface Store {

  /** Accesses a collection with the given [String] identifier. */
  fun collection(path: String): CollectionReference
}
