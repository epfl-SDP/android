package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.*
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

  /**
   * The sequence of boards, guaranteed to have a size of at least one. The current board is
   * available as [first].
   */
  private val boardSequence = boards.asReversed().asSequence()

  override val board: Board<Piece<Color>> = boards.last()

  override val nextStep: NextStep
    get() {
      val hasActions = boardSequence.hasAnyMoveAvailable(nextPlayer)
      val inCheck = boardSequence.inCheck(nextPlayer)
      if (!hasActions) {
        return if (inCheck) {
          NextStep.Checkmate(nextPlayer.other())
        } else {
          NextStep.Stalemate
        }
      } else {
        return NextStep.MovePiece(nextPlayer, inCheck) { action ->
          val (_, effects) =
              actionsAndEffects(action.from).firstOrNull { (it, _) -> action == it }
                  ?: return@MovePiece this

          val nextBoard = effects.perform(board)

          copy(
              previous = this to action,
              nextPlayer = nextPlayer.other(),
              boards = boards.add(nextBoard.toBoard()), // Flatten the Board.
          )
        }
      }
    }

  override fun actions(position: Position): Sequence<Action> =
      actionsAndEffects(position).map { it.first }

  /**
   * Returns a [Sequence] of [Action] and [Effect] that can be performed at the given [Position] by
   * the player [nextPlayer]. Actions which would result in a check position are ignored.
   */
  private fun actionsAndEffects(
      position: Position,
  ): Sequence<Pair<Action, Effect<Piece<Color>>>> =
      boardSequence.allMovesAtPosition(nextPlayer, position).filter { (_, effect) ->
        val next = effect.perform(board)
        !(sequenceOf(next) + boardSequence).inCheck(nextPlayer)
      }
}
