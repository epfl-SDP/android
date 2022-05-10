package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Piece as EnginePiece
import ch.epfl.sdp.mobile.application.chess.engine.Position as EnginePosition
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

interface GameChessBoardState : ChessBoardState<Piece> {

  /** The current [Game], which is updated when the [Match] progresses. */
  var game: Game

  override val pieces: Map<ChessBoardState.Position, Piece>
    get() = game.board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = game.nextStep
      if (nextStep !is NextStep.MovePiece || !nextStep.inCheck) return null
      return game.board
          .firstNotNullOf { (position, piece) ->
            position.takeIf { piece.color == nextStep.turn && piece.rank == Rank.King }
          }
          .toPosition()
    }

  /** Returns the available actions [from] a position [to] another. */
  fun availableActions(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
  ): List<Action> {
    return game.actions(EnginePosition(from.x, from.y))
        .filter { it.from + it.delta == EnginePosition(to.x, to.y) }
        .toList()
  }

  /**
   * An implementation of [Piece] which uses an [EnginePiece] internally.
   *
   * @param piece backing [EnginePiece].
   */
  data class Piece(private val piece: EnginePiece<Color>) : ChessBoardState.Piece {
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
    fun ChessBoardState.Color.toEngineColor(): Color =
        when (this) {
          ChessBoardState.Color.Black -> Color.Black
          ChessBoardState.Color.White -> Color.White
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
    fun ChessBoardState.Rank.toEngineRank(): Rank =
        when (this) {
          ChessBoardState.Rank.King -> Rank.King
          ChessBoardState.Rank.Queen -> Rank.Queen
          ChessBoardState.Rank.Rook -> Rank.Rook
          ChessBoardState.Rank.Bishop -> Rank.Bishop
          ChessBoardState.Rank.Knight -> Rank.Knight
          ChessBoardState.Rank.Pawn -> Rank.Pawn
        }

    /**
     * Maps an [EngineColor] to a [ChessBoardState.Color].
     *
     * @receiver the [EngineColor] to map.
     * @return the resulting [ChessBoardState.Color].
     */
    fun Color.toColor(): ChessBoardState.Color =
        when (this) {
          Color.Black -> ChessBoardState.Color.Black
          Color.White -> ChessBoardState.Color.White
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
    fun Rank.toRank(): ChessBoardState.Rank =
        when (this) {
          Rank.King -> ChessBoardState.Rank.King
          Rank.Queen -> ChessBoardState.Rank.Queen
          Rank.Rook -> ChessBoardState.Rank.Rook
          Rank.Bishop -> ChessBoardState.Rank.Bishop
          Rank.Knight -> ChessBoardState.Rank.Knight
          Rank.Pawn -> ChessBoardState.Rank.Pawn
        }
  }
}
