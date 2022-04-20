package ch.epfl.sdp.mobile.sharedTest

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

/**
 * Atomically updates the value of the [MutableStateFlow] and returns a computed value from the
 * [MutableStateFlow] state. This is a variation of the standard existing `updateAndGet` from
 * mutable state flows.
 *
 * @param T the type of the value of the [MutableStateFlow].
 * @param R the type of the return value.
 * @param function a function which maps the [MutableStateFlow] to its next state and the computed
 * value.
 * @return the computed value.
 */
inline fun <T, R> MutableStateFlow<T>.updateAndGetWithValue(function: (T) -> Pair<T, R>): R {
  while (true) {
    val prevValue = value
    val (nextValue, returnValue) = function(prevValue)
    if (compareAndSet(prevValue, nextValue)) {
      return returnValue
    }
  }
}
