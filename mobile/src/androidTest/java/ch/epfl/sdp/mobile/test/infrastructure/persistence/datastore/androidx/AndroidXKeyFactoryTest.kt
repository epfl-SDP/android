package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.androidx

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXKeyFactory
import org.junit.Assert.fail
import org.junit.Test

class AndroidXKeyFactoryTest {

  @Test
  fun given_keysWithSameName_when_compared_then_areAllEqual() {
    val factory = AndroidXKeyFactory
    val keys =
        listOf(
            factory.int("hello"),
            factory.double("hello"),
            factory.string("hello"),
            factory.boolean("hello"),
            factory.float("hello"),
            factory.long("hello"),
            factory.stringSet("hello"),
        )

    // Truth does not have assertions to check that all values are duplicates.
    for (k1 in keys) {
      for (k2 in keys) {
        if (k1 != k2) fail()
      }
    }
  }
}
