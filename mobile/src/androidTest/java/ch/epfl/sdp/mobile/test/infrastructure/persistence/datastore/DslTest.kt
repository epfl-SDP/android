package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.edit
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslTest {

  @Test
  fun given_emptyStore_when_readingAnyKey_then_returnsNull() = runTest {
    val (store, factory) = emptyDataStoreFactory().createPreferencesDataStore()
    val key = factory.boolean("hello")
    assertThat(store.data.first()[key]).isNull()
  }

  @Test
  fun given_emptyStore_when_writingAndReading_then_returnsWrittenData() = runTest {
    val (store, factory) = emptyDataStoreFactory().createPreferencesDataStore()
    val key = factory.boolean("hello")
    store.edit { it[key] = true }
    assertThat(store.data.first()[key]).isTrue()
  }

  @Test
  fun given_emptyStore_when_updatingAcrossEditBlocks_then_returnsLatestValue() = runTest {
    val (store, factory) = emptyDataStoreFactory().createPreferencesDataStore()
    val key = factory.boolean("hello")
    store.edit { it[key] = true }
    store.edit { it[key] = false }
    assertThat(store.data.first()[key]).isFalse()
  }

  @Test
  fun given_emptyStore_when_updatingWithinEditBlock_then_returnsLatestValue() = runTest {
    val (store, factory) = emptyDataStoreFactory().createPreferencesDataStore()
    val key = factory.boolean("hello")
    store.edit {
      it[key] = true
      it[key] = false
    }
    assertThat(store.data.first()[key]).isFalse()
  }
}
