package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.KeyFactory

/** An implementation of [KeyFactory] which only creates some [FakeKey]. */
object FakeKeyFactory : KeyFactory {
  override fun int(name: String) = FakeKey<Int>(name)
  override fun double(name: String) = FakeKey<Double>(name)
  override fun string(name: String) = FakeKey<String>(name)
  override fun boolean(name: String) = FakeKey<Boolean>(name)
  override fun float(name: String) = FakeKey<Float>(name)
  override fun long(name: String) = FakeKey<Long>(name)
  override fun stringSet(name: String) = FakeKey<Set<String>>(name)
}
