package ch.epfl.sdp.mobile.test.infrastructure.assets.fake

import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import java.io.Reader
import java.io.StringReader

/**
 * Represents a Fake Asset Manager, which allows to load assets given as constructor parameters in
 * different manners
 *
 * @param csvString The content of a csv file to "store" as an asset for later loading
 */
class FakeAssetManager(
    private val csvString: String,
) : AssetManager {

  /**
   * Opens a certain asset file as a [Reader]
   *
   * @param path The path to the asset (has no effects)
   */
  override fun openAsReader(path: String): Reader {
    return StringReader(csvString)
  }
}

/** Builds and returns a [AssetManager] with no data. */
fun emptyAssets(): AssetManager = FakeAssetManager(csvString = "")
