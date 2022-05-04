package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.application.chess.notation.UCINotation
import ch.epfl.sdp.mobile.ui.puzzles.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.SnapshotPuzzle
import com.opencsv.CSVReaderHeaderAware
import java.io.FileReader

/**
 * An interface which represents all the endpoints and available features for puzzles interactions
 * for a user of the Pawnies application.
 */
class PuzzleFacade() {

  suspend fun solvePuzzle(puzzle: Puzzle, user: AuthenticatedUser) {
    user.update { solvedPuzzles(puzzle) }
  }

  private fun allPuzzles(): List<Puzzle> {
    val reader = CSVReaderHeaderAware(FileReader("puzzles/puzzles.csv"))
    val csvMap = mutableListOf<Map<String, String>>()
    var line = reader.readMap()
    while (line != null) {
      csvMap.add(line)
      line = reader.readMap()
    }

    val puzzles =
        csvMap.map {
          val puzzleId = it["PuzzleId"] ?: "Error"
          val fen =
              FenNotation.parseFen(it["FEN"] ?: "")
                  ?: FenNotation.BoardSnapshot(
                      board = buildBoard {},
                      playing = Color.White,
                      castlingRights =
                          FenNotation.CastlingRights(
                              kingSideWhite = false,
                              queenSideWhite = false,
                              kingSideBlack = false,
                              queenSideBlack = false,
                          ),
                      enPassant = null,
                      halfMoveClock = -1,
                      fullMoveClock = -1,
                  )
          val moves = UCINotation.parseActions(it["Moves"] ?: "") ?: emptyList()
          val rating = (it["Rating"] ?: "-1").toInt()

          SnapshotPuzzle(
              uid = puzzleId,
              boardSnapshot = fen,
              puzzleMoves = moves,
              elo = rating,
          )
        }

    return puzzles
  }

  fun solvedPuzzles(profile: Profile): List<Puzzle> {
    return allPuzzles().filter { profile.solvedPuzzles.contains(it.uid) }
  }

  fun unsolvedPuzzles(profile: Profile): List<Puzzle> {
    return allPuzzles().filterNot { profile.solvedPuzzles.contains(it.uid) }
  }
}
