@file:Suppress("UNCHECKED_CAST")

package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentId
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentRecord
import com.google.firebase.firestore.DocumentId
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

/**
 * Returns true if the given [KClass] has a declared field of the given name and annotated with an
 * annotation of type [A]. Because [DocumentId] is a Java annotation, the Java APIs for reflection
 * have to be used.
 *
 * @receiver the [KClass] that the field is searched on.
 * @param name the name of the property.
 * @return true iff there is a field with the given name annotated by [A].
 */
private inline fun <reified A : Annotation> KClass<*>.hasJavaAnnotatedField(name: String): Boolean =
    try {
      java.getDeclaredField(name).getDeclaredAnnotation(A::class.java) != null
    } catch (noSuchField: NoSuchFieldException) {
      false
    }

/** The set of [kotlin.reflect.KType] that will be accepted for [DocumentId]-annotated fields. */
private val SupportedDocumentIdTypes = setOf(typeOf<String>(), typeOf<String?>())

/** The set of [kotlin.reflect.KType] that will be accepted as primitive values for documents. */
private val SupportedFieldTypes =
    setOf(
        typeOf<Boolean>(),
        typeOf<Boolean?>(),
        typeOf<Double>(),
        typeOf<Double?>(),
        typeOf<Float>(),
        typeOf<Float?>(),
        typeOf<Int>(),
        typeOf<Int?>(),
        typeOf<Long>(),
        typeOf<Long?>(),
        typeOf<Number>(),
        typeOf<Number?>(),
        typeOf<Short>(),
        typeOf<Short?>(),
        typeOf<String>(),
        typeOf<String?>(),
        // Collections.
        typeOf<List<*>>(),
        typeOf<List<*>?>(),
        // If the types are not specified, we can't automatically map them to their data classes.
        typeOf<Any>(),
        typeOf<Any?>(),
    )

/**
 * Returns an object of type [T] using the provided [FakeDocumentRecord].
 *
 * @param T the type of the returned object.
 *
 * @receiver the [FakeDocumentRecord] which is transformed.
 * @param id the unique identifier for this document.
 * @param valueClass the [KClass] of the object to build.
 *
 * @return the T corresponding to the object to be built.
 */
fun <T : Any> FakeDocumentRecord.toObject(id: FakeDocumentId, valueClass: KClass<T>): T {
  return fields.toObject(id, valueClass)
}

/**
 * Returns an object of type [T] using the provided [Map] of [FieldPath] to their associated values.
 *
 * @param T the type of the returned object.
 * @receiver the [Map] of field paths which is transformed.
 * @param id the unique identifier for this document.
 * @param valueClass the [KClass] of the object to build.
 *
 * @return the [T] corresponding to the object to be built.
 */
private fun <T : Any> Map<FieldPath, Any?>.toObject(id: FakeDocumentId, valueClass: KClass<T>): T {
  require(valueClass.isData) { "Only data classes are currently supported." }
  val constructor = requireNotNull(valueClass.primaryConstructor) { "Missing a constructor." }
  val arguments = mutableMapOf<KParameter, Any?>()
  for (parameter in constructor.parameters) {
    val name = requireNotNull(parameter.name) { "Unnamed constructor parameter not supported." }
    when {
      valueClass.hasJavaAnnotatedField<DocumentId>(name) -> {
        require(parameter.type in SupportedDocumentIdTypes) { "DocumentId must be a String?." }
        require(this[FieldPath(name)] == null) {
          "Found a document field with the same name as DocumentId."
        }
        arguments[parameter] = id.value
      }
      parameter.type.classifier in SupportedFieldTypes.map { it.classifier } ->
          arguments[parameter] = this[FieldPath(name)]
      else -> arguments[parameter] = tail().toObject(id, parameter.type.jvmErasure)
    }
  }
  return constructor.callBy(arguments)
}

/** Returns the tail of the [FieldPath], if it has at least two segments. */
private fun FieldPath.tail(): FieldPath? =
    if (segments.size > 1) FieldPath(segments.drop(1)) else null

/** Returns this [Map] from which all of the [FieldPath]'s prefixes are removed. */
private fun Map<FieldPath, Any?>.tail(): Map<FieldPath, Any?> =
    mapKeys { (k) -> k.tail() }.filterKeys { it != null }.mapKeys { (k) -> requireNotNull(k) }

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
  val fields = mutableMapOf<FieldPath, Any?>()
  for (property in valueClass.memberProperties) {
    if (valueClass.hasJavaAnnotatedField<DocumentId>(property.name)) {
      continue
    } else if (property.returnType.classifier in SupportedFieldTypes.map { it.classifier }) {
      fields[FieldPath(property.name)] = property.get(value)
    } else {
      val nested = property.get(value)
      if (nested != null) {
        val record = fromObject(nested, property.returnType.jvmErasure as KClass<Any>)
        record.fields.forEach { (k, v) ->
          fields[FieldPath(listOf(property.name) + k.segments)] = v
        }
      }
    }
  }
  return FakeDocumentRecord(fields)
}
