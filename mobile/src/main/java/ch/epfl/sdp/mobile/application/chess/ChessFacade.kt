package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.*
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.BlackWon
import ch.epfl.sdp.mobile.application.ChessMetadata.Companion.WhiteWon
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.parseFen
import ch.epfl.sdp.mobile.application.chess.notation.UCINotation.parseActions
import ch.epfl.sdp.mobile.application.chess.notation.mapToGame
import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import com.opencsv.CSVReaderHeaderAware
import kotlinx.coroutines.flow.*
import java.io.StringReader

/**
 * An interface which represents all the endpoints and available features for online chess
 * interactions for a user of the Pawnies application.
 *
 * @param auth the [Auth] instance which will be used to handle authentication.
 * @param store the [Store] which is used to manage documents.
 * @param assets the [AssetManager] which is used to load assets.
 */
class ChessFacade(
    private val auth: Auth,
    private val store: Store,
    private val assets: AssetManager,
) {

  /** Chess matches side of chess facade */

  /**
   * Creates a "local" [Match] for the [AuthenticatedUser] and stores it in the [Store]
   *
   * @param user The [AuthenticatedUser] that wants to create the [Match]
   *
   * @return The created [Match] before storing it in the [Store]
   */
  suspend fun createLocalMatch(user: AuthenticatedUser): Match {
    val document = store.collection("games").document()
    document.set(
      ChessDocument(
        whiteId = user.uid,
        blackId = user.uid,
        lastUpdatedAt = System.currentTimeMillis()
      )
    )
    return StoreMatch(document.id, store, user)
  }

  /**
   * Creates a [Match] between two [Profile]s and stores it in the [Store]
   *
   * @param white The [Profile] of the player that will play white
   * @param black The [Profile] of the player that will play black
   *
   * @return The created [Match] before storing it in the [Store]
   */
  suspend fun createMatch(white: Profile, black: Profile, user: Profile? = null): Match {
    val document = store.collection("games").document()
    document.set(
      ChessDocument(
        whiteId = white.uid, blackId = black.uid, lastUpdatedAt = System.currentTimeMillis()
      )
    )
    return StoreMatch(document.id, store, user)
  }

  /**
   * Returns the [Match] associated to the given identifier.
   *
   * @param id the unique identifier for this [Match].
   */
  fun match(id: String, user: Profile? = null): Match {
    return StoreMatch(id, store, user)
  }

  /**
   * Fetches a [Flow] of [List] of [Match]s that a certain [Profile] has going on with any other
   * player (or even himself)
   *
   * @param profile The [Profile] whose [Match]s will be fetched
   *
   * @return The [Flow] of [List] of [Match]s for the [Profile]
   */
  fun matches(profile: Profile): Flow<List<Match>> {
    val gamesAsWhite = getMatchesForPlayer(colorField = "whiteId", profile)
    val gamesAsBlack = getMatchesForPlayer(colorField = "blackId", profile)

    return combine(gamesAsWhite, gamesAsBlack) { (a, b) -> a.union(b).sortedBy { it.id } }
  }

  private fun getMatchesForPlayer(colorField: String, user: Profile): Flow<List<Match>> {
    return store
        .collection("games")
        .whereEquals(colorField, user.uid)
        .asMatchListFlow(user)
        .onStart { emit(emptyList()) }
  }

  private fun Query.asMatchListFlow(user: Profile? = null): Flow<List<Match>> {
    return this.asFlow<ChessDocument>().map {
      it.filterNotNull().mapNotNull(ChessDocument::uid).map { uid -> StoreMatch(uid, store, user) }
    }
  }

  /**
   * Fetches the list of all [Puzzle]s from their source
   *
   * As of now, the puzzles come from the Lichess.org Open Database
   * (https://database.lichess.org/#puzzles)
   */
  private fun allPuzzles(): List<Puzzle> {
    return sequence {
          val reader = CSVReaderHeaderAware(StringReader(assets.readText(csvPath)))
          while (true) {
            val line = reader.readMap() ?: return@sequence
            yield(line)
          }
        }
        .map {
          val puzzleId = it[csvPuzzleId] ?: return@map null
          val fen = parseFen(it[csvFen] ?: "") ?: return@map null
          val moves = parseActions(it[csvMoves] ?: "") ?: return@map null
          val rating = it[csvRating]?.toIntOrNull() ?: return@map null

          SnapshotPuzzle(
              uid = puzzleId,
              boardSnapshot = fen,
              puzzleMoves = moves,
              elo = rating,
          )
        }
        .filterNotNull()
        .toList()
  }

  /**
   * Gets a certain [Puzzle] by his uid
   *
   * @param uid The uid of the [Puzzle] to get
   *
   * @return The specified [Puzzle], if it exists
   */
  fun puzzle(uid: String): Puzzle? {
    return allPuzzles().firstOrNull { it.uid == uid }
  }

  /**
   * Fetches the list of solved [Puzzle]s for a certain [Profile]
   *
   * @param profile the [Profile] in question
   *
   * @returns The list of solved [Puzzle]s
   */
  fun solvedPuzzles(profile: Profile): List<Puzzle> {
    return allPuzzles().filter { profile.solvedPuzzles.contains(it.uid) }
  }

  /**
   * Fetches the list of unsolved [Puzzle]s for a certain [Profile]
   *
   * @param profile the [Profile] in question
   *
   * @returns The list of unsolved [Puzzle]s
   */
  fun unsolvedPuzzles(profile: Profile): List<Puzzle> {
    return allPuzzles().filterNot { profile.solvedPuzzles.contains(it.uid) }
  }
}

private data class SnapshotPuzzle(
    override val uid: String,
    override val boardSnapshot: FenNotation.BoardSnapshot,
    override val puzzleMoves: List<Action>,
    override val elo: Int,
) : Puzzle

private data class StoreMatch(
    override val id: String,
    private val store: Store,
    private val user: Profile?
) : Match {

  fun profile(
      uid: String,
  ): Flow<Profile?> {

    return store.collection("users").document(uid).asFlow<ProfileDocument>().map { doc ->
      doc?.toProfile(NotAuthenticatedUser)
    }
  }

  private val documentFlow = store.collection("games").document(id).asFlow<ChessDocument>()

  override val game = documentFlow.map { it?.moves ?: emptyList() }.mapToGame()

  override val white =
      documentFlow.map { it?.whiteId }.flatMapLatest {
        it?.let(this@StoreMatch::profile) ?: flowOf(null)
      }
  override val black =
      documentFlow.map { it?.blackId }.flatMapLatest {
        it?.let(this@StoreMatch::profile) ?: flowOf(null)
      }

  override suspend fun update(game: Game) {
    val document = store.collection("games").document(id).get<ChessDocument>()

    store.collection("games").document(id).update {
      this[FieldPath(listOf("metadata", "status"))] =
          when (val step = game.nextStep) {
            NextStep.Stalemate -> NextStep.Stalemate
            is NextStep.MovePiece -> null
            is NextStep.Checkmate -> if (step.winner == Color.Black) BlackWon else WhiteWon
          }

      if (document?.blackId == user?.uid) {
        this[FieldPath(listOf("metadata", "blackName"))] = user?.name
      }

      if (document?.whiteId == user?.uid) {
        this[FieldPath(listOf("metadata", "whiteName"))] = user?.name
      }

      this["moves"] = game.toAlgebraicNotation()
      this["lastUpdatedAt"] = System.currentTimeMillis()
    }
  }
}

private const val csvPath = "puzzles/puzzles.csv"
private const val csvPuzzleId = "PuzzleId"
private const val csvFen = "FEN"
private const val csvMoves = "Moves"
private const val csvRating = "Rating"
