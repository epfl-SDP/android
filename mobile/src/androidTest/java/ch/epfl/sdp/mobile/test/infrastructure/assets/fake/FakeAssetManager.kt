package ch.epfl.sdp.mobile.test.infrastructure.assets.fake

import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import java.io.Reader
import java.io.StringReader

class FakeAssetManager(
    private val csvString: String,
) : AssetManager {
  override fun openAsReader(path: String): Reader {
    return StringReader(csvString)
  }
}

/** Builds and returns a [AssetManager] with no data. */
fun emptyAssets(): AssetManager = FakeAssetManager(csvString = "")
