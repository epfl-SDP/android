package ch.epfl.sdp.mobile.state.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.MovableChessBoardState

/**
 * An abstract implementation of [MovableChessBoardState] which delegates the updates of the game to
 * its [GameChessBoardState].
 *
 * @param delegate the [GameChessBoardState] delegate.
 */
abstract class AbstractMovableChessBoardState(
    delegate: GameChessBoardState,
) : MovableChessBoardState<GameChessBoardState.Piece>, GameChessBoardState by delegate {

  /**
   * Attempts to perform a move from the given [ChessBoardState.Position] to the given
   * [ChessBoardState.Position]. If the move can't be performed, this will result in a no-op.
   *
   * @param from the start [ChessBoardState.Position].
   * @param to the end [ChessBoardState.Position].
   */
  abstract fun tryPerformMove(from: ChessBoardState.Position, to: ChessBoardState.Position)

  final override var selectedPosition by mutableStateOf<ChessBoardState.Position?>(null)
    private set

  override val availableMoves: Set<ChessBoardState.Position>
    // Display all the possible moves for all the pieces on the board.
    get() {
      val position = selectedPosition ?: return emptySet()
      return game.actions(Position(position.x, position.y))
          .mapNotNull { it.from + it.delta }
          .map { it.toPosition() }
          .toSet()
    }

  override fun onDropPiece(
      piece: GameChessBoardState.Piece,
      endPosition: ChessBoardState.Position,
  ) {
    val startPosition = pieces.entries.firstOrNull { it.value == piece }?.key ?: return
    selectedPosition = null
    tryPerformMove(startPosition, endPosition)
  }

  override fun onPositionClick(position: ChessBoardState.Position) {
    val from = selectedPosition
    if (from == null) {
      selectedPosition = position
    } else {
      selectedPosition = null
      tryPerformMove(from, position)
    }
  }
}
