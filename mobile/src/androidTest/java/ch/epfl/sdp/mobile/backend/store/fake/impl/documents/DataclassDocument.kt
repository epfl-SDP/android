package ch.epfl.sdp.mobile.backend.store.fake.impl.documents

import ch.epfl.sdp.mobile.backend.store.fake.UpdatePolicy
import kotlin.reflect.full.instanceParameter

class DataClassUpdatePolicy(
    private val factory: () -> Any,
) : UpdatePolicy<Any?> {
  override val empty: Any? = null
  override fun Any?.update(fields: Map<String, Any?>): Any {

    // Create the document if it's missing.
    var from: Any = this ?: factory()

    // Get an instance of the copy() method on data classes.
    val copyMethod = from::class.members.first { it.name == "copy" }

    // For each field, call the copy method to update the argument.
    for ((field, value) in fields) {
      val param = copyMethod.parameters.first { it.name == field }
      val instance = requireNotNull(copyMethod.instanceParameter)

      // Call the method on the current from instance, with the updated param.
      from = requireNotNull(copyMethod.callBy(mapOf(instance to from, param to value)))
    }
    return from
  }
}
