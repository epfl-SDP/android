package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.ui.game.ChessBoardCells

/**
 * Deserializes and creates a [Game] from a [List] of [String]s in long algebraic notation
 *
 * @return The deserialized [Game]
 */
fun List<String>.deserialize(): Game {
  var game = Game.create()

  for (move in this) {
    val (position, delta) = parseStringToMove(move)
    game = (game.nextStep as? NextStep.MovePiece)?.move?.invoke(position, delta) ?: game
  }
  return game
}

/**
 * Serializes a [Match] to a [List] of [String]s in long algebraic chess notation
 *
 * @return The serialized [List] of [String]s
 */
fun Game.serialize(): List<String> {
  val sequence = sequence {
    var previous = this@serialize.previous
    while (previous != null) {
      val (game, action) = previous
      val str = parseMoveToString(game, action)
      yield(str)
      previous = game.previous
    }
  }

  return sequence.toList().asReversed()
}

/** Parsing string to moves */
private fun parseStringToMove(move: String): Pair<Position, Delta> {

  val length = move.length
  val fromStr = move.subSequence(length - 5, length - 3).toString()
  val toStr = move.subSequence(length - 2, length).toString()

  val from = chessNotationToPosition(fromStr)
  val to = chessNotationToPosition(toStr)
  val delta = Delta(to.x - from.x, to.y - from.y)

  return from to delta
}

private fun chessNotationToPosition(pos: String): Position {
  val x = pos[0].code - 'a'.code
  val y = (ChessBoardCells - 1) - (pos[1].code - '1'.code)

  return Position(x, y)
}

/** Parsing moves to string */
private fun parseMoveToString(game: Game, action: Action): String {
  val (from, delta) = action

  val pieceFrom = game.board[from]

  val to = from.plus(delta)
  val pieceTo = to?.let { game.board[it] }

  val pieceStr = pieceToChessNotation(pieceFrom)
  val fromStr = positionToChessNotation(from)
  val interStr = if (pieceTo == null) "-" else "x"
  val toStr = positionToChessNotation(to)

  return pieceStr + fromStr + interStr + toStr
}

private fun pieceToChessNotation(piece: Piece<Color>?): String {
  return when (piece?.rank) {
    Rank.King -> "K"
    Rank.Queen -> "Q"
    Rank.Rook -> "R"
    Rank.Bishop -> "B"
    Rank.Knight -> "N"
    Rank.Pawn -> ""
    else -> "?"
  }
}

private fun positionToChessNotation(pos: Position?): String {
  return if (pos == null || !pos.inBounds) {
    "?"
  } else {
    val col = (pos.x.toChar() + 'a'.code).toString()
    val row = ChessBoardCells - pos.y
    col + row
  }
}
