package ch.epfl.sdp.mobile.infrastructure.assets.android

import android.content.Context
import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import java.io.Reader

class AndroidAssetManager(
    private val context: Context,
) : AssetManager {
  override fun openAsReader(path: String): Reader {
    return context.assets.open(path).reader()
  }
}
