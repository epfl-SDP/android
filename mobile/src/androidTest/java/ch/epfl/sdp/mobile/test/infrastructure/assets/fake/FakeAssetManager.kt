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

/**
 * Builds an [AssetManager] with a single puzzle and returns a pair of it and a list containing the
 * puzzle's id.
 */
fun onePuzzleAssets(): Pair<AssetManager, List<String>> {
  val assets =
      FakeAssetManager(
          "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
              "00008,r6k/pp2r2p/4Rp1Q/3p4/8/1N1P2R1/PqP2bPP/7K b - - 0 24,f2g3 e6e7 b2b1 b3c1 b1c1 h6c1,1852,74,97,1444,crushing hangingPiece long middlegame,https://lichess.org/787zsVup/black#48\n")

  val puzzles = listOf("00008")

  return assets to puzzles
}

/**
 * Builds an [AssetManager] with two puzzles and returns a pair of it and a list containing the
 * puzzle's id.
 */
fun twoPuzzleAssets(): Pair<AssetManager, List<String>> {
  val assets =
      FakeAssetManager(
          "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
              "00008,r6k/pp2r2p/4Rp1Q/3p4/8/1N1P2R1/PqP2bPP/7K b - - 0 24,f2g3 e6e7 b2b1 b3c1 b1c1 h6c1,1852,74,97,1444,crushing hangingPiece long middlegame,https://lichess.org/787zsVup/black#48\n" +
              "0000D,5rk1/1p3ppp/pq3b2/8/8/1P1Q1N2/P4PPP/3R2K1 w - - 2 27,d3d6 f8d8 d6d8 f6d8,1580,73,97,11995,advantage endgame short,https://lichess.org/F8M8OS71#53\n")

  val puzzles = listOf("00008", "0000D")

  return assets to puzzles
}
