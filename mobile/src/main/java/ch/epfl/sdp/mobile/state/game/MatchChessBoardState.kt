package ch.epfl.sdp.mobile.state.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Color as EngineColor
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.Piece as EnginePiece
import ch.epfl.sdp.mobile.application.chess.engine.Position as EnginePosition
import ch.epfl.sdp.mobile.application.chess.engine.Rank as EngineRank
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A [ChessBoardState] which uses a [Match] to implement the display of pieces through
 * [ChessBoardState].
 *
 * @param match the [Match] which should be displayed.
 * @param scope the [CoroutineScope] in which the match is observed.
 */
class MatchChessBoardState(
    match: Match,
    scope: CoroutineScope,
) : ChessBoardState<Piece> {

  /** The current [Game], which is updated when the [Match] progresses. */
  var game by mutableStateOf(Game.create())
    private set

  init {
    scope.launch { match.game.collect { game = it } }
  }

  override val pieces: Map<ChessBoardState.Position, Piece>
    get() = game.board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = game.nextStep
      if (nextStep !is NextStep.MovePiece || !nextStep.inCheck) return null
      return game.board
          .firstNotNullOf { (position, piece) ->
            position.takeIf { piece.color == nextStep.turn && piece.rank == EngineRank.King }
          }
          .toPosition()
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
