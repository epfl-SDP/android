package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake

/**
 * A class representing the unique identifier of a [FakeDocumentRecord]. Within a collection, all
 * documents are guaranteed to have different ids.
 *
 * @param value the value of the unique identifier.
 */
data class FakeDocumentId(val value: String) {

  companion object {

    /** The [FakeDocumentId] given to the root of the store. */
    val Root = FakeDocumentId("")
  }
}
