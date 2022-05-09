package ch.epfl.sdp.mobile.infrastructure.assets.android

import android.content.Context
import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import java.io.Reader

/**
 * Represents the Android Assets Manager, which allows to load assets in different manners
 *
 * @param context The Android [Context] used to load assets
 */
class AndroidAssetManager(
    private val context: Context,
) : AssetManager {
  override fun openAsReader(path: String): Reader {
    return context.assets.open(path).reader()
  }
}
