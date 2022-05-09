package ch.epfl.sdp.mobile.infrastructure.assets

import java.io.Reader

/** Represents an Asset Manager, which allows to load assets in different manners */
interface AssetManager {

  /**
   * Opens a certain asset file as a [Reader]
   *
   * @param path The path to the asset
   *
   * @return the opened Reader
   */
  fun openAsReader(path: String): Reader
}
