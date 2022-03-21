package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization

import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentId
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentRecord
import com.google.firebase.firestore.DocumentId
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
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
  require(valueClass.isData) { "Only data classes are currently supported." }
  val constructor = requireNotNull(valueClass.primaryConstructor) { "Missing a constructor." }
  val arguments = mutableMapOf<KParameter, Any?>()
  for (parameter in constructor.parameters) {
    val name = requireNotNull(parameter.name) { "Unnamed constructor parameter not supported." }
    if (valueClass.hasJavaAnnotatedField<DocumentId>(name)) {
      require(parameter.type in SupportedDocumentIdTypes) { "DocumentId must be a String?." }
      require(fields[name] == null) { "Found a document field with the same name as DocumentId." }
      arguments[parameter] = id.value
    } else {
      arguments[parameter] = fields[name]
    }
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
    if (!valueClass.hasJavaAnnotatedField<DocumentId>(property.name)) {
      fields[property.name] = property.get(value)
    }
  }
  return FakeDocumentRecord(fields)
}
