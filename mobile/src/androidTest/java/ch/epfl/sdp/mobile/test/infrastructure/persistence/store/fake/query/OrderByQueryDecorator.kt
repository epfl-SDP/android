package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentSnapshot
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlin.Comparator
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A decorator which orders results according to a certain [Comparator] on the resulting document
 * snapshots.
 *
 * @param query the query to decorate.
 * @param field the field to compare.
 * @param order the direction of the comparison.
 */
class OrderByQueryDecorator(
    private val query: FakeQuery,
    private val field: String,
    private val order: Query.Direction,
) : FakeQuery {

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot> {
    val comparator =
        Comparator<FakeDocumentSnapshot> { a, b ->
          Comparator.compare(a?.record?.fields?.get(field), b?.record?.fields?.get(field))
        }
    val withOrder = if (order == Query.Direction.Ascending) comparator else comparator.reversed()
    return query.asQuerySnapshotFlow().map {
      it.copy(documents = it.documents.sortedWith(withOrder))
    }
  }
}

// The Firestore reference [1] defines a strict ordering for mixed types, written below. Currently
// unsupported data types by the FakeStore are marked as [UNSUPPORTED]. Unsupported types will throw
// some exceptions when being compared.
//
//  1. Null values
//  2. Boolean values, with false < true
//  3. Integer and floating point values, sorted in numerical order
//  4. [UNSUPPORTED] Date values
//  5. Text string values
//  6. [UNSUPPORTED] Byte values
//  7. [UNSUPPORTED] Cloud Firestore references
//  8. [UNSUPPORTED] Geographical point values, by latitude then longitude
//  9. [UNSUPPORTED] Array values, by lexicographic order
// 10. [UNSUPPORTED] Map values, by keys then by values
//
// [1] : https://firebase.google.com/docs/firestore/manage-data/data-types

/** The array of KType, sorted by their relative importance. */
private val TypesAscending = arrayOf(typeOf<Boolean>(), typeOf<Number>(), typeOf<String>())

// Ordering values.
private const val SmallestFirst = -1
private const val Equal = 0
private const val GreatestFirst = 1

/** The [Comparator] which should be used to perform some comparisons across the generic values. */
private val Comparator: Comparator<Any?> = Comparator { a, b ->

  // Null values are always the smallest.
  if (a == null && b == null) return@Comparator Equal
  if (a == null) return@Comparator SmallestFirst
  if (b == null) return@Comparator GreatestFirst

  // Retrieve the type indices of both a and b. If either of them is a null value, throw an
  // exception, since we don't support matching on these types yet.
  val aType = a::class.createType()
  val bType = b::class.createType()
  val aTypeIndex = TypesAscending.indexOfFirstOrNull { it.isSupertypeOf(aType) } ?: badType(aType)
  val bTypeIndex = TypesAscending.indexOfFirstOrNull { it.isSupertypeOf(bType) } ?: badType(bType)

  // Ensure both indices are well-defined and in the table.
  return@Comparator when {
    aTypeIndex == bTypeIndex && aType.isSubtypeOf(TypesAscending[0]) -> compareBoolean(a, b)
    aTypeIndex == bTypeIndex && aType.isSubtypeOf(TypesAscending[1]) -> compareNumber(a, b)
    aTypeIndex == bTypeIndex && bType.isSubtypeOf(TypesAscending[2]) -> compareString(a, b)
    else -> bTypeIndex - aTypeIndex // Both indices are different
  }
}

/**
 * Indicates that the given type is not supported.
 *
 * @param type the unsupported type.
 */
private fun badType(
    type: KType?,
): Nothing = error("Type $type is not supported in FakeQuery.orderBy yet.")

/**
 * Returns the first index matching the given predicate, or null.
 *
 * @param T the type of the elements of the array.
 * @return the [Array] for which the predicate is checked.
 * @param predicate the predicate to check for.
 * @return the nullable index.
 */
private inline fun <T> Array<out T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
  val index = indexOfFirst(predicate = predicate)
  return if (index == -1) null else index
}

/** Compares the values [a] and [b] as booleans. */
private fun compareBoolean(a: Any, b: Any): Int {
  a as Boolean
  b as Boolean
  return a.compareTo(b)
}

/** Compares the values [a] and [b] as numbers. */
private fun compareNumber(a: Any, b: Any): Int {
  a as Number
  b as Number
  return a.toDouble().compareTo(b.toDouble())
}

/** Compares the values [a] and [b] as strings. */
private fun compareString(a: Any, b: Any): Int {
  a as String
  b as String
  return a.compareTo(b)
}
