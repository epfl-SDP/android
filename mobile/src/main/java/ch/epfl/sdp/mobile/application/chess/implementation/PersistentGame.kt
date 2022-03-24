package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.Rank.King
import ch.epfl.sdp.mobile.application.chess.rules.*
import kotlinx.collections.immutable.PersistentList

/**
 * A persistent implementation of a [Game], which contains some information about the current board
 * positions, the past moves that were performed and the possible next steps.
 *
 * @param nextPlayer the [Color] of the next player to play.
 */
data class PersistentGame(
    override val previous: Pair<PersistentGame, Action>?,
    val nextPlayer: Color,
    private val boards: PersistentList<Board<Piece<Color>>>,
) : Game {

  private val boardSequence = boards.asReversed().asSequence()

  override val board: Board<Piece<Color>> = boards.last()

  override val nextStep: NextStep
    get() =
        NextStep.MovePiece(nextPlayer) { from, delta ->
          val (action, effects) =
              actionsAndEffects(from).firstOrNull { (action, _) ->
                action.from == from && action.delta == delta
              }
                  ?: return@MovePiece this

          val nextBoard = effects.perform(board)

          copy(
              previous = this to nextPlayer.normalize(action),
              nextPlayer = nextPlayer.other(),
              boards = boards.add(nextBoard),
          )
        }

  override fun actions(position: Position): Sequence<Action> =
      actionsAndEffects(position).map { it.first }

  private fun actionsAndEffects(
      position: Position,
  ): Sequence<Pair<Action, Effect<Piece<Color>>>> =
      boardSequence.allMovesAtPosition(nextPlayer, position).filter { (_, effect) ->
        val next = effect.perform(board)
        !(sequenceOf(next) + boardSequence).inCheck(nextPlayer)
      }
}

/** Returns true iff the [Board] has at least one piece of the given [Rank] and [Color]. */
private fun <Color> Board<Piece<Color>>.hasAnyOfRankAndColor(rank: Rank, player: Color): Boolean {
  return asSequence().any { (_, piece) -> piece.rank == rank && piece.color == player }
}

private fun BoardSequence<Piece<Color>>.inCheck(player: Color): Boolean {
  return allMoves(player.other()).any { (_, effect) ->
    !effect.perform(first()).hasAnyOfRankAndColor(King, player)
  }
}

private fun BoardSequence<Piece<Color>>.allMoves(color: Color): Moves<Piece<Color>> {
  return first().asSequence().flatMap { (pos, _) -> allMovesAtPosition(color, pos) }
}

private fun BoardSequence<Piece<Color>>.allMovesAtPosition(
    color: Color,
    position: Position
): Moves<Piece<Color>> {
  val normalizedBoards = map { NormalizedBoardDecorator(color, it) }
  val normalizedFrom = color.normalize(position)
  val piece = normalizedBoards.first()[normalizedFrom] ?: return emptySequence()
  if (piece.color == Role.Adversary) return emptySequence()
  val moves = piece.rank.moves(normalizedBoards, normalizedFrom)
  return moves.map { (action, effect) -> color.normalize(action) to color.denormalize(effect) }
}
