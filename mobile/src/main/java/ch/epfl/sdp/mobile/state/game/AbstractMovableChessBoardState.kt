package ch.epfl.sdp.mobile.state.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.MovableChessBoardState

/**
 * An abstract implementation of [MovableChessBoardState] which delegates the updates of the game to
 * its [GameDelegate].
 *
 * @param delegate the [GameDelegate] delegate.
 */
abstract class AbstractMovableChessBoardState(
    private val delegate: GameDelegate,
) :
    MovableChessBoardState<DelegatingChessBoardState.Piece>,
    ChessBoardState<DelegatingChessBoardState.Piece> by DelegatingChessBoardState(delegate) {

  /**
   * Attempts to perform a move from the given [ChessBoardState.Position] to the given
   * [ChessBoardState.Position]. If the move can't be performed, this will result in a no-op.
   *
   * @param from the start [ChessBoardState.Position].
   * @param to the end [ChessBoardState.Position].
   */
  abstract fun move(from: ChessBoardState.Position, to: ChessBoardState.Position)

  final override var selectedPosition by mutableStateOf<ChessBoardState.Position?>(null)
    private set

  override val availableMoves: Set<ChessBoardState.Position>
    // Display all the possible moves for all the pieces on the board.
    get() {
      val position = selectedPosition ?: return emptySet()
      return delegate
          .game
          .actions(Position(position.x, position.y))
          .map { it.from + it.delta }
          .map { it.toPosition() }
          .toSet()
    }

  override fun onDropPiece(
      piece: DelegatingChessBoardState.Piece,
      endPosition: ChessBoardState.Position,
  ) {
    val startPosition = pieces.entries.firstOrNull { it.value == piece }?.key ?: return
    selectedPosition = null
    move(startPosition, endPosition)
  }

  override fun onPositionClick(position: ChessBoardState.Position) {
    val from = selectedPosition
    if (from == null) {
      selectedPosition = position
    } else {
      selectedPosition = null
      move(from, position)
    }
  }
}
