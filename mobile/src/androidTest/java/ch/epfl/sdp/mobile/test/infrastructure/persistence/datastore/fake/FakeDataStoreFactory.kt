package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStoreFactory

/**
 * A fake implementation of [DataStoreFactory] which always returns the same underlying instance of
 * [FakePreferencesDataStore].
 */
object FakeDataStoreFactory : DataStoreFactory {

  /** The underlying [FakePreferencesDataStore]. */
  private val preferences = FakePreferencesDataStore()

  override fun createPreferencesDataStore() = preferences to FakeKeyFactory
}
