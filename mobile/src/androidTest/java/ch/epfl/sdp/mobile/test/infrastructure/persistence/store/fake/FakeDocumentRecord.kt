package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * A [FakeDocumentRecord] which contains all the fields from a document. Document records do not
 * enforce a specific shape.
 *
 * @param fields the fields of the record.
 */
data class FakeDocumentRecord(val fields: Map<String, Any?>) {

  /** A convenience construction which builds an empty [FakeDocumentRecord]. */
  constructor() : this(emptyMap())

  /**
   * Updates the [FakeDocumentRecord] using the given [scope], starting from the given value. This
   * returns a new immutable record.
   *
   * @param scope the [DocumentEditScope] lambda which should be applied.
   */
  fun update(scope: DocumentEditScope.() -> Unit): FakeDocumentRecord {
    val recorded = RecordingDocumentEditScope().apply(scope)
    return copy(fields = fields + recorded.mutations)
  }

  companion object
}

/**
 * An implementation of [DocumentEditScope] which records the mutations that were performed on the
 * scope. Mutations which affect the same field will be replaced with their latest value.
 */
private class RecordingDocumentEditScope : DocumentEditScope {

  /** The mutations which have been applied within the [DocumentEditScope]. */
  val mutations: Map<String, Any?>
    get() = recording

  /** The map which records the mutations. */
  private val recording = mutableMapOf<String, Any?>()

  override fun set(field: String, value: Any?) {
    recording[field] = value
  }
}

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
