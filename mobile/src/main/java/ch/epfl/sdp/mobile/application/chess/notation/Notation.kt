package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.ui.game.ChessBoardCells

/**
 * Deserializes and creates a [Match] from a [ChessDocument]
 *
 * For a null [ChessDocument], a default [Match] with null value and default [Game] positions will
 * be created
 *
 * @return The deserialized [Match]
 */
fun ChessDocument?.deserialize(): Match {
  var game = Game.create()

  for (move in this?.moves ?: emptyList()) {
    val (position, delta) = parseStringToMove(move)
    game = (game.nextStep as? NextStep.MovePiece)?.move?.invoke(position, delta) ?: game
  }
  return Match(game, this?.uid, this?.whiteId, this?.blackId)
}


/**
 * Serializes a [Match] to a [ChessDocument]
 *
 * @return The serialized [ChessDocument]
 */
fun Match.serialize(): ChessDocument {
  val sequence = sequence {
    var previous = this@serialize.game.previous
    while (previous != null) {
      val (game, action) = previous
      val str = parseMoveToString(game, action)
      yield(str)
      previous = game.previous
    }
  }

  return ChessDocument(
    moves = sequence.toList().asReversed(), whiteId = this.whiteId, blackId = this.blackId)
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