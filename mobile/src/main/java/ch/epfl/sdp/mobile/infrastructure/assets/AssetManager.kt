package ch.epfl.sdp.mobile.infrastructure.assets

import java.io.Reader

interface AssetManager {
  fun openAsReader(path: String): Reader
}
