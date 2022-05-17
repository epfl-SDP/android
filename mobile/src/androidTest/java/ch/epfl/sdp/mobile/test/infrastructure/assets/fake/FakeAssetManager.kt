package ch.epfl.sdp.mobile.test.infrastructure.assets.fake

import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager

/**
 * Represents a Fake Asset Manager, which allows to load assets given as constructor parameters in
 * different manners
 *
 * @param csvString The content of a csv file to "store" as an asset for later loading
 */
class FakeAssetManager(
    private val csvString: String,
) : AssetManager {
  override fun readText(path: String): String? {
    return csvString
  }
}

/** Builds and returns a [AssetManager] with no data. */
fun emptyAssets(): AssetManager =
    FakeAssetManager(
        csvString = "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n")
