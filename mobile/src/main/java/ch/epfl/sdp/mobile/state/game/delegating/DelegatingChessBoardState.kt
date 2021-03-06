package ch.epfl.sdp.mobile.state.game.delegating

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color as EngineColor
import ch.epfl.sdp.mobile.application.chess.engine.Piece as EnginePiece
import ch.epfl.sdp.mobile.application.chess.engine.Position as EnginePosition
import ch.epfl.sdp.mobile.application.chess.engine.Rank as EngineRank
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/**
 * An implementation of [ChessBoardState] which uses a [GameDelegate] to extract the chess board
 * information.
 *
 * @property delegate the underlying [GameDelegate].
 */
class DelegatingChessBoardState(private val delegate: GameDelegate) : ChessBoardState<Piece> {

  override val pieces: Map<ChessBoardState.Position, Piece>
    get() = delegate.game.board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  /**
   * Returns the position of the king of the given color, if it exists.
   *
   * @param color the color of the king we're looking for.
   * @return the [ChessBoardState.Position] of the king.
   */
  private fun kingPosition(color: ChessBoardState.Color): ChessBoardState.Position? =
      delegate
          .game
          .board
          .firstNotNullOfOrNull { (position, piece) ->
            position.takeIf {
              piece.color == color.toEngineColor() && piece.rank == EngineRank.King
            }
          }
          ?.toPosition()

  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = delegate.game.nextStep
      return when {
        nextStep is NextStep.Checkmate -> kingPosition(nextStep.winner.other().toColor())
        nextStep is NextStep.MovePiece && nextStep.inCheck -> kingPosition(nextStep.turn.toColor())
        else -> null
      }
    }

  override val lastMove: Set<ChessBoardState.Position>
    get() {
      val previousStep = delegate.game.previous ?: return emptySet()
      val lastAction = previousStep.second

      return setOf(
          lastAction.from.toPosition(),
          lastAction.let {
            val lastPosition = it.from.plus(it.delta)
            lastPosition.toPosition()
          },
      )
    }

  /**
   * An implementation of [Piece] which uses an [EnginePiece] internally.
   *
   * @param piece backing [EnginePiece].
   */
  data class Piece(private val piece: EnginePiece<EngineColor>) : ChessBoardState.Piece {
    override val rank = piece.rank.toRank()
    override val color = piece.color.toColor()
  }

  companion object {

    /**
     * Maps a [ChessBoardState.Color] to an [EngineColor].
     *
     * @receiver the [ChessBoardState.Color] to map.
     * @return the resulting [EngineColor].
     */
    fun ChessBoardState.Color.toEngineColor(): EngineColor =
        when (this) {
          ChessBoardState.Color.Black -> EngineColor.Black
          ChessBoardState.Color.White -> EngineColor.White
        }

    /**
     * Maps a [ChessBoardState.Position] to an [EnginePosition].
     *
     * @receiver the [ChessBoardState.Position] to map.
     * @return the resulting [EnginePosition].
     */
    fun ChessBoardState.Position.toEnginePosition(): EnginePosition = EnginePosition(x, y)

    /**
     * Maps a [ChessBoardState.Rank] to an [EngineRank].
     *
     * @receiver the [ChessBoardState.Rank] to map.
     * @return the resulting [EngineRank].
     */
    fun ChessBoardState.Rank.toEngineRank(): EngineRank =
        when (this) {
          ChessBoardState.Rank.King -> EngineRank.King
          ChessBoardState.Rank.Queen -> EngineRank.Queen
          ChessBoardState.Rank.Rook -> EngineRank.Rook
          ChessBoardState.Rank.Bishop -> EngineRank.Bishop
          ChessBoardState.Rank.Knight -> EngineRank.Knight
          ChessBoardState.Rank.Pawn -> EngineRank.Pawn
        }

    /**
     * Maps an [EngineColor] to a [ChessBoardState.Color].
     *
     * @receiver the [EngineColor] to map.
     * @return the resulting [ChessBoardState.Color].
     */
    fun EngineColor.toColor(): ChessBoardState.Color =
        when (this) {
          EngineColor.Black -> ChessBoardState.Color.Black
          EngineColor.White -> ChessBoardState.Color.White
        }

    /**
     * Maps an [EnginePosition] to a [ChessBoardState.Position].
     *
     * @receiver the [EnginePosition] to map.
     * @return the resulting [ChessBoardState.Position].
     */
    fun EnginePosition.toPosition(): ChessBoardState.Position = ChessBoardState.Position(x, y)

    /**
     * Maps an [EngineRank] to a [ChessBoardState.Rank].
     *
     * @receiver the [EngineRank] to map.
     * @return the resulting [ChessBoardState.Rank].
     */
    fun EngineRank.toRank(): ChessBoardState.Rank =
        when (this) {
          EngineRank.King -> ChessBoardState.Rank.King
          EngineRank.Queen -> ChessBoardState.Rank.Queen
          EngineRank.Rook -> ChessBoardState.Rank.Rook
          EngineRank.Bishop -> ChessBoardState.Rank.Bishop
          EngineRank.Knight -> ChessBoardState.Rank.Knight
          EngineRank.Pawn -> ChessBoardState.Rank.Pawn
        }
  }
}
