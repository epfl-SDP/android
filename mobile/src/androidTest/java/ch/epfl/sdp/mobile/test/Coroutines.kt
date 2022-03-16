package ch.epfl.sdp.mobile.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine

/** Returns any value by suspending forever. */
suspend inline fun suspendForever(): Nothing = suspendCancellableCoroutine {}

/**
 * Combines the [Collection] of [Flow] using the standard [combine] operator, using an empty list if
 * the collection of flows is empty.
 *
 * (see https://github.com/Kotlin/kotlinx.coroutines/issues/1603 for more information about why this
 * is not the default behavior for [combine]).
 *
 * @param T the type of the flow items.
 *
 * @receiver the [Collection] of [Flow] which is combined.
 * @return a [Flow] of [R], transformed using the function.
 */
inline fun <reified T> Collection<Flow<T>>.combineOrEmpty(): Flow<List<T>> {
  if (isEmpty()) return flowOf(emptyList())
  return combine(this) { it.toList() }
}
