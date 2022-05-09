package ch.epfl.sdp.mobile.infrastructure.assets

/** Represents an Asset Manager, which allows to load assets in different manners */
interface AssetManager {

  /**
   * Opens a certain asset file as a [String] of its content, if successful.
   *
   * @param path The path to the asset
   *
   * @return the [String] of the content of the opened file, if successful.
   */
  fun readText(path: String): String?
}
