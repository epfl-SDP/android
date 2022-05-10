package ch.epfl.sdp.mobile.test.infrastructure.assets.fake

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FakeAssetManagerTest {

  @Test
  fun given_fakeAssetManagerWithString_when_readingReaderString_then_isCorrectString() {
    val assets = FakeAssetManager("Test string")

    val res = assets.readText("whatever")
    assertThat(res).isEqualTo("Test string")
  }
}
