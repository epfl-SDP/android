package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.state.SnapshotChessBoardState.SnapshotPiece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.Move
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of [GameScreenState] that starts with default chess positions, can move pieces
 * and has a static move list
 */
class SnapshotChessBoardState(
    gameState: State<Game>,
    private val scope: CoroutineScope,
    private val uploadGame: suspend (Game) -> Unit,
) : GameScreenState<SnapshotPiece> {
  private val game by gameState

  /**
   * An implementation of [ChessBoardState.Piece] which uses a [PieceIdentifier] to disambiguate
   * different pieces.
   *
   * @param id the unique [PieceIdentifier].
   * @param color the color for the piece.
   * @param rank the rank for the piece.
   */
  data class SnapshotPiece(
      val id: PieceIdentifier,
      override val color: ChessBoardState.Color,
      override val rank: ChessBoardState.Rank,
  ) : ChessBoardState.Piece

  override val pieces: Map<ChessBoardState.Position, SnapshotPiece>
    get() =
        Position.all()
            .map { game.board[it]?.let { p -> it to p } }
            .filterNotNull()
            .map { (a, b) -> a.toPosition() to b.toPiece() }
            .toMap()

  override val availableMoves: Set<ChessBoardState.Position>
    // Display all the possible moves for all the pieces on the board.
    get() =
        Position.all()
            .flatMap { game.actions(it) }
            .mapNotNull { it.from + it.delta }
            .map { it.toPosition() }
            .toSet()

  override fun onDropPiece(piece: SnapshotPiece, endPosition: ChessBoardState.Position) {
    val startPosition = pieces.entries.firstOrNull { it.value == piece }?.key ?: return
    val step = game.nextStep as NextStep.MovePiece

    scope.launch {
      uploadGame(
          step.move(
              Position(startPosition.x, startPosition.y),
              Delta(endPosition.x - startPosition.x, endPosition.y - startPosition.y),
          ),
      )
    }
  }

  override val moves: List<Move> =
      listOf(
          ChessMove(1, "f3"),
          ChessMove(2, "e5"),
          ChessMove(3, "g4"),
          ChessMove(4, "Qh4#"),
      )
}

/**
 * Represents a [ChessBoardState]'s Move
 * @property number The move's number in the game's history
 * @property name The move's name in chess notation
 */
data class ChessMove(override val number: Int, override val name: String) : Move

/** Maps a game engine [Position] to a [ChessBoardState.Position] */
private fun Position.toPosition(): ChessBoardState.Position {
  return ChessBoardState.Position(this.x, this.y)
}

private fun Piece<Color>.toPiece(): SnapshotPiece {
  val rank =
      when (this.rank) {
        Rank.King -> ChessBoardState.Rank.King
        Rank.Queen -> ChessBoardState.Rank.Queen
        Rank.Rook -> ChessBoardState.Rank.Rook
        Rank.Bishop -> ChessBoardState.Rank.Bishop
        Rank.Knight -> ChessBoardState.Rank.Knight
        Rank.Pawn -> ChessBoardState.Rank.Pawn
      }

  val color =
      when (this.color) {
        Color.Black -> ChessBoardState.Color.Black
        Color.White -> ChessBoardState.Color.White
      }

  return SnapshotPiece(id = this.id, rank = rank, color = color)
}

/**
 * The [StatefulGameScreen] to be used for the Navigation
 *
 * @param user the currently logged-in user.
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()

  // TODO: User userId and opponentId to find correct game
  val gameId = "sample"

  val game = remember { chessFacade.game(gameId) }.collectAsState(Game.create())

  suspend fun updateGameWithId(game: Game) {
    chessFacade.updateGame(gameId, game)
  }

  val gameScreenState =
      remember(game, scope) { SnapshotChessBoardState(game, scope, ::updateGameWithId) }

  GameScreen(gameScreenState, modifier)
}
