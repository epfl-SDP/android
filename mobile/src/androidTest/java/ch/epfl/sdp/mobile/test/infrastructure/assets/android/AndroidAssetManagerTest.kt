package ch.epfl.sdp.mobile.test.infrastructure.assets.android

import android.content.Context
import ch.epfl.sdp.mobile.infrastructure.assets.android.AndroidAssetManager
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import java.io.*
import org.junit.Test

class AndroidAssetManagerTest {


  fun given_androidAssetManager_when__then_() {
    val context = mockk<Context>()
    every { context.assets.open(any()).reader() } returns
        InputStreamReader(ByteArrayInputStream("Test".encodeToByteArray()))

    val assets = AndroidAssetManager(context)

    val res = assets.openAsReader("any path")
    assertThat(res.readText()).isEqualTo("Test")
  }
}
