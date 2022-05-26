package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake.FakeDataStoreFactory

/** Returns a [DataStoreFactory] with a fake implementation. */
fun emptyDataStoreFactory(): DataStoreFactory = FakeDataStoreFactory()
