package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.state.SnapshotChessBoardState.SnapshotPiece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Move
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The [StatefulGameScreen] to be used for the Navigation
 *
 * @param user the currently logged-in user.
 * @param id the identifier for the match.
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    id: String,
    modifier: Modifier = Modifier,
) {
  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val match = remember(facade, id) { facade.match(id) }

  val gameScreenState = remember(user, match, scope) { SnapshotChessBoardState(user, match, scope) }

  GameScreen(gameScreenState, modifier)
}

/**
 * An implementation of [GameScreenState] that starts with default chess positions, can move pieces
 * and has a static move list
 */
class SnapshotChessBoardState(
    private val user: AuthenticatedUser,
    private val match: Match,
    private val scope: CoroutineScope,
) : GameScreenState<SnapshotPiece> {

  // TODO : Implement these things.
  override fun onArClick() = Unit
  override var listening by mutableStateOf(false)
    private set
  override fun onListenClick() {
    listening = !listening
  }

  private var game by mutableStateOf(Game.create())
  private var whiteProfile by mutableStateOf<Profile?>(null)
  private var blackProfile by mutableStateOf<Profile?>(null)

  override val white: GameScreenState.Player
    get() = GameScreenState.Player(whiteProfile?.name, "Check !")

  override val black: GameScreenState.Player
    get() = GameScreenState.Player(blackProfile?.name, null)

  init {
    scope.launch { match.game.collect { game = it } }
    scope.launch { match.white.collect { whiteProfile = it } }
    scope.launch { match.black.collect { blackProfile = it } }
  }

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

  /** The currently selected [Position] of the board. */
  override var selectedPosition by mutableStateOf<ChessBoardState.Position?>(null)
    private set

  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = game.nextStep
      if (nextStep !is NextStep.MovePiece || !nextStep.inCheck) return null
      return game.board
          .first { (_, piece) -> piece.color == nextStep.turn && piece.rank == Rank.King }
          .first
          .toPosition()
    }

  override val pieces: Map<ChessBoardState.Position, SnapshotPiece>
    get() =
        game.board.asSequence().map { (pos, piece) -> pos.toPosition() to piece.toPiece() }.toMap()

  override val availableMoves: Set<ChessBoardState.Position>
    // Display all the possible moves for all the pieces on the board.
    get() {
      val position = selectedPosition ?: return emptySet()
      return game.actions(Position(position.x, position.y))
          .mapNotNull { it.from + it.delta }
          .map { it.toPosition() }
          .toSet()
    }

  override fun onDropPiece(piece: SnapshotPiece, endPosition: ChessBoardState.Position) {
    val startPosition = pieces.entries.firstOrNull { it.value == piece }?.key ?: return
    tryPerformMove(startPosition, endPosition)
  }

  override fun onPositionClick(position: ChessBoardState.Position) {
    val from = selectedPosition
    if (from == null) {
      selectedPosition = position
    } else {
      tryPerformMove(from, position)
    }
  }

  /**
   * Attempts to perform a move from the given [ChessBoardState.Position] to the given
   * [ChessBoardState.Position]. If the move can't be performed, this will result in a no-op.
   *
   * @param from the start [ChessBoardState.Position].
   * @param to the end [ChessBoardState.Position].
   */
  private fun tryPerformMove(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
  ) {
    // Hide the current selection.
    selectedPosition = null

    val step = game.nextStep as? NextStep.MovePiece ?: return

    val currentPlayingId =
        when (step.turn) {
          Color.Black -> blackProfile?.uid
          Color.White -> whiteProfile?.uid
        }

    if (currentPlayingId == user.uid) {
      // TODO: Update game locally first, then verify upload was successful?
      scope.launch {
        val newGame =
            step.move(
                Position(from.x, from.y),
                Delta(to.x - from.x, to.y - from.y),
            )
        match.update(newGame)
      }
    }
  }

  override val moves: List<Move> =
      listOf(
          Move("f3"),
          Move("e5"),
          Move("g4"),
          Move("Qh4#"),
      )
}

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
