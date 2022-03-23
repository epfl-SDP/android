package ch.epfl.sdp.mobile.application

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.rules.Action
import ch.epfl.sdp.mobile.ui.game.ChessBoardCells
import com.google.firebase.firestore.DocumentId

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @param uid the unique identifier for this profile.
 * @param name the human-readable name associated to this profile.
 * @param emoji the emoji associated with this profile.
 * @param backgroundColor the hex color code for this profile.
 * @param followers a list of unique identifiers of the users who follow this profile.
 */
data class ProfileDocument(
    @DocumentId val uid: String? = null,
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null,
    val followers: List<String>? = null,
)

fun ProfileDocument?.toProfile(): Profile {
  return object : Profile {
    override val emoji: String = this@toProfile?.emoji ?: "😎"
    override val name: String = this@toProfile?.name ?: ""
    override val backgroundColor: Profile.Color =
        this@toProfile?.backgroundColor?.let(Profile::Color) ?: Profile.Color.Default
    override val uid: String = this@toProfile?.uid ?: ""
  }
}

/**
 * A document which represents a game of chess between two users. All the game documents are stored
 * in the `/games/` collection.
 *
 * @param moves
 * @param whiteId
 * @param blackId
 */
data class ChessDocument(
    @DocumentId val uid: String? = null,
    val moves: List<String>? = null,
    val whiteId: String? = null,
    val blackId: String? = null,
)

fun ChessDocument?.deserialize(): Game {
  var game = Game.create()

  for (move in this?.moves ?: emptyList()) {
    val (position, delta) = parseStringToMove(move)
    game = (game.nextStep as? NextStep.MovePiece)?.move?.invoke(position, delta) ?: game
  }
  return game
}

fun Game.serialize(whiteId: String? = null, blackId: String? = null): ChessDocument {
  val sequence = sequence {
    var previous = this@serialize.previous
    while (previous != null) {
      val (game, action) = previous
      val str = parseMoveToString(game, action)
      yield(str)
      previous = game.previous
    }
  }

  return ChessDocument(moves = sequence.toList().asReversed(), whiteId = whiteId, blackId = blackId)
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
