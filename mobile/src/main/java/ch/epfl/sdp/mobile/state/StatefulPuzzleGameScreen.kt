package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.engine.Rank.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.SnapshotChessBoardState.SnapshotPiece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.puzzles.Puzzle
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The [StatefulPuzzleGameScreen] to be used for the Navigation
 *
 * @param user the currently logged-in user.
 * @param puzzleId the identifier for the puzzle.
 * @param modifier the [Modifier] for the composable.
 * @param paddingValues the [PaddingValues] for this composable.
 */
@Composable
fun StatefulPuzzleGameScreen(
    user: AuthenticatedUser,
    puzzleId: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) {
  val facade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val puzzle = remember(facade, puzzleId) { Puzzle(puzzleId) }

  val puzzleGameScreenState =
      remember(user, puzzle, scope) {
        SnapshotPuzzleBoardState(
            user = user,
            puzzle = puzzle,
            scope = scope,
        )
      }

  PuzzleGameScreen(
      state = puzzleGameScreenState,
      modifier = modifier,
      contentPadding = paddingValues,
  )
}

/**
 * An implementation of [GameScreenState] that starts with default chess positions, can move pieces
 * and has a static move list.
 *
 * @param user the currently authenticated user.
 * @param puzzle the currently loaded puzzle.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 */
class SnapshotPuzzleBoardState(
    private val user: AuthenticatedUser,
    private val puzzle: Puzzle,
    private val scope: CoroutineScope,
) : PuzzleGameScreenState<SnapshotPiece> {

  private var game by mutableStateOf(dummyPuzzle())

  override val puzzleId = puzzle.uid

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

  private fun tryPerformMove(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
  ) {
    // Hide the current selection.
    selectedPosition = null

    val step = game.nextStep as? NextStep.MovePiece ?: return

    val playingColor =
        when (step.turn) {
          Black -> ChessBoardState.Color.Black
          White -> ChessBoardState.Color.White
        }

    if (playingColor == puzzle.playerColor) {
      val actions =
        game.actions(Position(from.x, from.y))
          .filter { it.from + it.delta == Position(to.x, to.y) }
          .toList()

      if (actions.size == 1) {
        scope.launch {
          game = step.move(actions.first())
        }
      } else {
        promotionFrom = from
        promotionTo = to
        choices = actions.filterIsInstance<Action.Promote>().map { it.rank.toChessBoardStateRank() }
      }
      if (correctPuzzleMove(from, to)) {
        game = step.move(Action())
      } else {
        // Warn user that it was not the correct move
        game = dummyPuzzle()
      }
    }
  }

  private fun correctPuzzleMove(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
  ): Boolean {
    return (from.x == 7 && from.y == 2 && to.x == 7 && to.y == 1)
  }

  private fun dummyPuzzle(): Game {
    return buildGame(White) {
      var id = PieceIdentifier(0)

      set(Position(1, 0), Piece(Black, Rook, id++))
      set(Position(6, 0), Piece(Black, King, id++))
      set(Position(2, 1), Piece(White, Rook, id++))
      set(Position(7, 2), Piece(White, Pawn, id++))
      set(Position(0, 3), Piece(Black, Pawn, id++))
      set(Position(5, 3), Piece(White, King, id++))
      set(Position(6, 3), Piece(White, Bishop, id++))
      set(Position(6, 4), Piece(White, Pawn, id++))
      set(Position(0, 5), Piece(Black, Rook, id++))
      set(Position(1, 6), Piece(Black, Pawn, id++))
    }
  }

  // TODO: Use game's version
  /** Maps a game engine [Position] to a [ChessBoardState.Position] */
  private fun Position.toPosition(): ChessBoardState.Position {
    return ChessBoardState.Position(this.x, this.y)
  }
  // TODO: Use game's version
  private fun Piece<Color>.toPiece(): SnapshotPiece {
    val rank =
        when (this.rank) {
          King -> ChessBoardState.Rank.King
          Queen -> ChessBoardState.Rank.Queen
          Rook -> ChessBoardState.Rank.Rook
          Bishop -> ChessBoardState.Rank.Bishop
          Knight -> ChessBoardState.Rank.Knight
          Pawn -> ChessBoardState.Rank.Pawn
        }

    val color =
        when (this.color) {
          Black -> ChessBoardState.Color.Black
          White -> ChessBoardState.Color.White
        }

    return SnapshotPiece(id = this.id, rank = rank, color = color)
  }
}
