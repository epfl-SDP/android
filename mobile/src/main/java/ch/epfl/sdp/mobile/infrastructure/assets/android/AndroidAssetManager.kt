package ch.epfl.sdp.mobile.infrastructure.assets.android

import android.content.Context
import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager

/**
 * Represents the Android Assets Manager, which allows to load assets in different manners.
 *
 * @param context The Android [Context] used to load assets.
 */
class AndroidAssetManager(
    private val context: Context,
) : AssetManager {
  override fun readText(path: String): String? {
    return runCatching { context.assets.open(path).reader().use { it.readText() } }.getOrNull()
  }
}
