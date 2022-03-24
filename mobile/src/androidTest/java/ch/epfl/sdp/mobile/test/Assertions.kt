package ch.epfl.sdp.mobile.test

import com.google.common.truth.Truth
import org.junit.Assert

/**
 * Asserts that the given [block] throws an exception or an error of type [T].
 *
 * @param T the expected type of the [Throwable].
 * @param block the [block] under test.
 */
inline fun <reified T : Throwable> assertThrows(block: () -> Unit) {
  var error = false
  try {
    block()
    error = true
  } catch (throwable: Throwable) {
    if (throwable is T) {
      Truth.assertThat(throwable).isInstanceOf(T::class.java)
    }
  }
  if (error) {
    Assert.fail("Expected an exception of type ${T::class.qualifiedName} but got nothing.")
  }
}
