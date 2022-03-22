package ch.epfl.sdp.mobile.application

import ch.epfl.sdp.mobile.application.chess.Delta
import ch.epfl.sdp.mobile.application.chess.Game
import ch.epfl.sdp.mobile.application.chess.NextStep
import ch.epfl.sdp.mobile.application.chess.Position

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @param name the human-readable name associated to this profile.
 * @param emoji the emoji associated with this profile.
 * @param backgroundColor the hex color code for this profile.
 */
data class ProfileDocument(
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null
)

fun ProfileDocument?.toProfile(): Profile {
  return object : Profile {
    override val emoji: String = this@toProfile?.emoji ?: "😎"
    override val name: String = this@toProfile?.name ?: ""
    override val backgroundColor: Profile.Color =
        this@toProfile?.backgroundColor?.let(Profile::Color) ?: Profile.Color.Default
  }
}

/**
 * A document which represents a game of chess between two users. All the game documents are stored
 * in the `/games/` collection.
 *
 * @param moves
 * @param whiteId
 * @param blackId
 *
 */
data class ChessDocument(
    val moves: List<String>? = null,
    val whiteId: String? = null,
    val blackId: String? = null,
)

fun ChessDocument?.deserialize(): Game {
  var game = Game.create()

  if (this?.moves == null) {
    return game
  }

  for (move in this.moves) {
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
      val str = parseMoveToString(action.from to action.delta)
      yield(str)
      previous = game.previous
    }
  }

  return ChessDocument(sequence.toList().asReversed(), whiteId, blackId)
}

/** A temporary function to parse ultra-basic moves in the form of (fromX; fromY; deltaX; deltaY) */
private fun parseStringToMove(move: String): Pair<Position, Delta> {
  val (posX, posY, delX, delY) = move.split(";")

  val position = Position(posX.toInt(), posY.toInt())
  val delta = Delta(delX.toInt(), delY.toInt())

  return position to delta
}

/** A temporary function to parse ultra-basic moves to the form of (fromX; fromY; deltaX; deltaY) */
private fun parseMoveToString(move: Pair<Position, Delta>): String {
  val (position, delta) = move
  return "${position.x};${position.y};${delta.x};${delta.y}"
}
