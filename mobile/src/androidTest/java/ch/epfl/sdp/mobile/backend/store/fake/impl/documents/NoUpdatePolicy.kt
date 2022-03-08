package ch.epfl.sdp.mobile.backend.store.fake.impl.documents

import ch.epfl.sdp.mobile.backend.store.fake.UpdatePolicy

class NoUpdatePolicy(value: Any?) : UpdatePolicy<Any?> {
  override val empty: Any? = value
  override fun Any?.update(fields: Map<String, Any?>): Any? = this
}
