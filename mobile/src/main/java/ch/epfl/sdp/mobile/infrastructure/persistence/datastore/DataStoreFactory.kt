package ch.epfl.sdp.mobile.infrastructure.persistence.datastore

/** An interface which may be used to create some [DataStore] implementations. */
interface DataStoreFactory {

  /**
   * Returns the default [DataStore] for storing some [Preferences], as well as the [KeyFactory]
   * associated with this [DataStore] instance.
   */
  fun createPreferencesDataStore(): Pair<DataStore<Preferences>, KeyFactory>
}
