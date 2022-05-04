package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.NotationCombinators.action
import ch.epfl.sdp.mobile.ui.game.ChessBoardCells

/**
 * An object which contains some utilities to transform games in extended game notation, and
 * vice-versa.
 */
object Notation {

  /**
   * Parses an [Action] from the provided [String] text.
   *
   * @param text the [String] that should be parsed.
   * @return the [Action] that was found, or null if an error occurred.
   */
  fun parseAction(text: String): Action? {
    return action().parse(input = text).firstOrNull()?.output
  }

  /**
   * Transforms this [Position] to extended algebraic notation.
   *
   * @receiver the [Position] that is transformed.
   */
  private fun Position.toExtendedNotation(): String {
    return if (!inBounds) {
      "?"
    } else {
      val col = (x.toChar() + 'a'.code).toString()
      val row = ChessBoardCells - y
      col + row
    }
  }

  /**
   * Transforms this [Rank] to extended algebraic notation.
   *
   * @receiver the [Rank] that is transformed.
   */
  private fun Rank.toExtendedNotation(): String =
      when (this) {
        Rank.King -> "K"
        Rank.Queen -> "Q"
        Rank.Rook -> "R"
        Rank.Bishop -> "B"
        Rank.Knight -> "N"
        Rank.Pawn -> ""
      }

  /**
   * Transforms this [Action] to extended algebraic notation.
   *
   * @receiver the [Action] that is transformed.
   * @param board the current state of the [Board].
   */
  fun Action.toExtendedNotation(board: Board<Piece<Color>>): String {
    val fromPosition = from
    val toPosition = from + delta ?: return "?"
    val rank = board[fromPosition]?.rank?.toExtendedNotation() ?: return "?"
    val from = fromPosition.toExtendedNotation()
    val sep = if (board[toPosition] != null) "x" else "-"
    val to = toPosition.toExtendedNotation()
    return when (this) {
      is Action.Move -> "$rank$from$sep$to"
      is Action.Promote -> "$rank$from$sep$to${this.rank.toExtendedNotation()}"
    }
  }

  /**
   * Parses a [Game] from a [List] of moves. Invalid moves will be ignored.
   *
   * @param text the [List] of [String] that should be parsed.
   * @param initial the initial [Game] to which the valid actions are incrementally applied.
   * @return the [Game] to which the moves were applied.
   */
  fun parseGame(text: List<String>, initial: Game = Game.create()): Game {
    var game = initial
    for (move in text) {
      val action = parseAction(move) ?: continue
      game = (game.nextStep as? NextStep.MovePiece)?.move?.invoke(action) ?: game
    }
    return game
  }

  /**
   * Transforms this [Game] to extended algebraic notation.
   *
   * @receiver the [Game] that is transformed.
   */
  fun Game.toExtendedNotation(): List<String> =
      sequence {
            var previous = previous
            while (previous != null) {
              val (game, action) = previous
              yield(action.toExtendedNotation(game.board))
              previous = game.previous
            }
          }
          .toList()
          .asReversed()
}
