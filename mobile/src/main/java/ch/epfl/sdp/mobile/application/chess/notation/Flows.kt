package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Returns the longest common prefix length between two [List].
 *
 * @param a the first [List] to inspect.
 * @param b the second [List] to inspect.
 */
private fun prefixLength(a: List<*>, b: List<*>): Int =
    a.asSequence().zip(b.asSequence()).takeWhile { (a, b) -> (a == b) }.count()

/**
 * A specialized [Flow] operator which incrementally maps some lists of [String] moves to the
 * resulting chess [Game].
 *
 * @receiver a [Flow] of the list of the moves.
 * @return a [Flow] of the latest [Game] state.
 */
fun Flow<List<String>>.mapToGame(): Flow<Game> = flow {
  var game = Game.create()
  var moves = emptyList<String>()
  collect { list ->
    val prefix = prefixLength(moves, list)
    if (prefix == moves.size) {
      // Incremental update.
      for (move in list.drop(prefix)) {
        val (position, delta) = parseStringToMove(move)
        game = (game.nextStep as? NextStep.MovePiece)?.move?.invoke(position, delta) ?: game
      }
    } else {
      // Total update.
      game = list.deserialize()
    }
    moves = list
    emit(game)
  }
}
