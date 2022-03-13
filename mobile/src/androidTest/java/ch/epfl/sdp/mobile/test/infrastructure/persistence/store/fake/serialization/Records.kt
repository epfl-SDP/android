package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization

import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentRecord
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Returns an object of type [T] using the provided [FakeDocumentRecord].
 *
 * @param T the type of the returned object.
 *
 * @receiver the [FakeDocumentRecord] which is transformed.
 * @param valueClass the [KClass] of the object to build.
 *
 * @return the T corresponding to the object to be built.
 */
fun <T : Any> FakeDocumentRecord.toObject(valueClass: KClass<T>): T {
  require(valueClass.isData) { "Only data classes are currently supported." }
  val constructor = requireNotNull(valueClass.primaryConstructor) { "Missing a constructor." }
  val arguments = mutableMapOf<KParameter, Any?>()
  for (parameter in constructor.parameters) {
    val name = requireNotNull(parameter.name) { "Unnamed constructor parameter not supported." }
    arguments[parameter] = fields[name]
  }
  return constructor.callBy(arguments)
}

/**
 * Returns a [FakeDocumentRecord] of using the provided [value].
 *
 * @param T the type of the object which will be stored.
 * @param valueClass the [KClass] of the object to be stored.
 *
 * @return the [FakeDocumentRecord] corresponding to the object.
 */
fun <T : Any> FakeDocumentRecord.Companion.fromObject(
    value: T,
    valueClass: KClass<T>,
): FakeDocumentRecord {
  require(valueClass.isData) { "Only data classes are currently supported." }
  val fields = mutableMapOf<String, Any?>()
  for (property in valueClass.memberProperties) {
    fields[property.name] = property.get(value)
  }
  return FakeDocumentRecord(fields)
}
